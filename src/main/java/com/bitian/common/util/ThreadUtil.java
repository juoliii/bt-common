package com.bitian.common.util;


import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工具类，提供统一的线程池管理能力。
 * <p>
 * 内部维护一个通用线程池 {@link #executor} 和一个调度线程池 {@link #scheduledExecutor}，
 * 支持异步执行、提交任务、批量等待、定时调度等常见场景。
 * </p>
 *
 * <h3>线程池参数说明</h3>
 * <ul>
 *   <li>核心线程数 = CPU核心数 × 4</li>
 *   <li>最大线程数 = CPU核心数 × 8</li>
 *   <li>空闲线程存活时间 = 60s</li>
 *   <li>任务队列容量 = 2048</li>
 *   <li>拒绝策略 = CallerRunsPolicy（由调用线程执行，避免任务丢失）</li>
 * </ul>
 */
public class ThreadUtil {

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_SIZE = CPU_COUNT * 4;
    private static final int MAX_CORE_SIZE = CPU_COUNT * 8;
    private static final int QUEUE_CAPACITY = 2048;
    private static final long KEEP_ALIVE_SECONDS = 60L;
    private static final String THREAD_NAME_PREFIX = "bt-common-pool-";
    private static final String SCHEDULED_THREAD_NAME_PREFIX = "bt-common-sched-";

    /**
     * 通用线程池。
     * <p>保留为 public 以兼容已有调用方，但建议优先使用本类提供的静态方法。</p>
     */
    public static final ThreadPoolExecutor executor = createExecutor();

    /**
     * 调度线程池，用于延迟执行与周期性任务。
     */
    private static final ScheduledExecutorService scheduledExecutor = createScheduledExecutor();

    static {
        // JVM 关闭时优雅停机
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            scheduledExecutor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
                if (!scheduledExecutor.awaitTermination(10, TimeUnit.SECONDS)) {
                    scheduledExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                scheduledExecutor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }, "bt-common-shutdown"));
    }

    private ThreadUtil() {
        // 工具类禁止实例化
    }

    // ==================== 线程池工厂方法 ====================

    private static ThreadPoolExecutor createExecutor() {
        ThreadPoolExecutor pool = new ThreadPoolExecutor(
                CORE_SIZE,
                MAX_CORE_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                newThreadFactory(THREAD_NAME_PREFIX),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
        // 允许核心线程超时回收，在长时间空闲时释放资源
        pool.allowCoreThreadTimeOut(true);
        return pool;
    }

    private static ScheduledExecutorService createScheduledExecutor() {
        return new ScheduledThreadPoolExecutor(
                Math.max(2, CPU_COUNT),
                newThreadFactory(SCHEDULED_THREAD_NAME_PREFIX),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    private static ThreadFactory newThreadFactory(String prefix) {
        AtomicInteger counter = new AtomicInteger(1);
        return r -> {
            Thread t = new Thread(r, prefix + counter.getAndIncrement());
            t.setDaemon(true);
            return t;
        };
    }

    // ==================== 基本执行方法（保持原有签名） ====================

    /**
     * 异步执行一个 Runnable 任务。
     *
     * @param runable 待执行的任务
     */
    public static void executeThread(Runnable runable) {
        executor.execute(runable);
    }

    /**
     * 提交一个 Callable 任务并返回 Future。
     *
     * @param callable 待执行的任务
     * @param <T>      返回值类型
     * @return 任务的 Future
     */
    public static <T> Future<T> submitThread(Callable<T> callable) {
        return executor.submit(callable);
    }

    /**
     * 提交一个 Runnable 任务并返回 Future。
     *
     * @param runnable 待执行的任务
     * @return 任务的 Future
     */
    public static Future<?> submitThread(Runnable runnable) {
        return executor.submit(runnable);
    }

    /**
     * 批量提交任务并阻塞等待全部完成。
     * <p>如果任意任务执行出错，将抛出 {@link RuntimeException}。</p>
     *
     * @param list 任务列表
     * @param <T>  返回值类型
     */
    public static <T> void submitAndWaitForFinish(List<Callable<T>> list) {
        try {
            List<Future<T>> fs = executor.invokeAll(list);
            for (Future<T> f : fs) {
                try {
                    f.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Task execution failed", e.getCause());
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while waiting for tasks", e);
        }
    }

    // ==================== 扩展执行方法 ====================

    /**
     * 批量提交任务并阻塞等待全部完成，带超时控制。
     *
     * @param list    任务列表
     * @param timeout 超时时间
     * @param unit    时间单位
     * @param <T>     返回值类型
     * @return 所有任务的 Future 列表（可能包含未完成的 Future）
     */
    public static <T> List<Future<T>> submitAndWaitForFinish(List<Callable<T>> list, long timeout, TimeUnit unit) {
        try {
            return executor.invokeAll(list, timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted while waiting for tasks", e);
        }
    }

    /**
     * 提交一组任务，返回最先完成的任务的结果。
     *
     * @param tasks 任务集合
     * @param <T>   返回值类型
     * @return 最先完成的任务结果
     */
    public static <T> T invokeAny(Collection<Callable<T>> tasks) {
        try {
            return executor.invokeAny(tasks);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted during invokeAny", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("All tasks failed in invokeAny", e.getCause());
        }
    }

    /**
     * 提交一组任务，在指定超时内返回最先完成的任务结果。
     *
     * @param tasks   任务集合
     * @param timeout 超时时间
     * @param unit    时间单位
     * @param <T>     返回值类型
     * @return 最先完成的任务结果
     */
    public static <T> T invokeAny(Collection<Callable<T>> tasks, long timeout, TimeUnit unit) {
        try {
            return executor.invokeAny(tasks, timeout, unit);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Thread was interrupted during invokeAny", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("All tasks failed in invokeAny", e.getCause());
        } catch (TimeoutException e) {
            throw new RuntimeException("invokeAny timed out after " + timeout + " " + unit, e);
        }
    }

    // ==================== 定时调度方法 ====================

    /**
     * 延迟执行一个任务。
     *
     * @param runnable 待执行的任务
     * @param delay    延迟时间
     * @param unit     时间单位
     * @return ScheduledFuture
     */
    public static ScheduledFuture<?> schedule(Runnable runnable, long delay, TimeUnit unit) {
        return scheduledExecutor.schedule(runnable, delay, unit);
    }

    /**
     * 延迟执行一个 Callable 任务。
     *
     * @param callable 待执行的任务
     * @param delay    延迟时间
     * @param unit     时间单位
     * @param <T>      返回值类型
     * @return ScheduledFuture
     */
    public static <T> ScheduledFuture<T> schedule(Callable<T> callable, long delay, TimeUnit unit) {
        return scheduledExecutor.schedule(callable, delay, unit);
    }

    /**
     * 以固定频率周期性执行任务。
     *
     * @param runnable     待执行的任务
     * @param initialDelay 首次延迟
     * @param period       执行间隔
     * @param unit         时间单位
     * @return ScheduledFuture
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period, TimeUnit unit) {
        return scheduledExecutor.scheduleAtFixedRate(runnable, initialDelay, period, unit);
    }

    /**
     * 以固定延迟周期性执行任务（上一次执行结束后延迟指定时间再执行下一次）。
     *
     * @param runnable     待执行的任务
     * @param initialDelay 首次延迟
     * @param delay        执行间隔
     * @param unit         时间单位
     * @return ScheduledFuture
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay, TimeUnit unit) {
        return scheduledExecutor.scheduleWithFixedDelay(runnable, initialDelay, delay, unit);
    }

    // ==================== 线程工具方法 ====================

    /**
     * 让当前线程休眠指定时间，自动处理 InterruptedException。
     *
     * @param millis 休眠毫秒数
     * @return 如果正常结束返回 true；如果被中断返回 false
     */
    public static boolean sleep(long millis) {
        try {
            Thread.sleep(millis);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 让当前线程休眠指定时间，自动处理 InterruptedException。
     *
     * @param duration 休眠时间
     * @param unit     时间单位
     * @return 如果正常结束返回 true；如果被中断返回 false
     */
    public static boolean sleep(long duration, TimeUnit unit) {
        return sleep(unit.toMillis(duration));
    }

    // ==================== 线程池状态方法 ====================

    /**
     * 获取线程池状态信息（用于监控/日志）。
     *
     * @return 格式化的线程池状态字符串
     */
    public static String getPoolStats() {
        return String.format(
                "ThreadPool[active=%d, poolSize=%d, core=%d, max=%d, queue=%d, completed=%d]",
                executor.getActiveCount(),
                executor.getPoolSize(),
                executor.getCorePoolSize(),
                executor.getMaximumPoolSize(),
                executor.getQueue().size(),
                executor.getCompletedTaskCount()
        );
    }

    /**
     * 线程池是否已关闭。
     *
     * @return true 表示已关闭
     */
    public static boolean isShutdown() {
        return executor.isShutdown();
    }

    /**
     * 手动关闭线程池（等待任务完成）。
     *
     * @param timeout 等待超时时间
     * @param unit    时间单位
     * @return true 表示在超时前所有任务已完成
     */
    public static boolean shutdown(long timeout, TimeUnit unit) {
        executor.shutdown();
        scheduledExecutor.shutdown();
        try {
            boolean a = executor.awaitTermination(timeout, unit);
            boolean b = scheduledExecutor.awaitTermination(timeout, unit);
            return a && b;
        } catch (InterruptedException e) {
            executor.shutdownNow();
            scheduledExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 立即关闭线程池，尝试取消所有正在执行的任务。
     *
     * @return 未执行完的任务列表
     */
    public static List<Runnable> shutdownNow() {
        scheduledExecutor.shutdownNow();
        return executor.shutdownNow();
    }
}
