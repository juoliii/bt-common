package com.bitian.common.util;


import java.util.List;
import java.util.concurrent.*;

public class ThreadUtil {

	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
	private static final int CORE_SIZE = CPU_COUNT * 4;
	private static final int MAX_CORE_SIZE = CPU_COUNT * 8;

	public static ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_CORE_SIZE*10);
	
	public static void executeThread(Runnable runable){
		executor.execute(runable);
	}

	public static <T> Future<T> submitThread(Callable<T> callable){
		return executor.submit(callable);
	}

	public static <T> void submitAndWaitForFinish(List<Callable<T>> list) {
		try {
			List<Future<T>> fs=executor.invokeAll(list);
			fs.forEach(item-> {
				try {
					item.get();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				} catch (ExecutionException e) {
					throw new RuntimeException(e);
				}
			});
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
