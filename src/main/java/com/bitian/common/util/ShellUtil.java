package com.bitian.common.util;

import com.bitian.common.dto.MyCommand;
import org.apache.commons.exec.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Shell 命令执行工具类。
 * <p>
 * 提供多种重载方法，支持：
 * <ul>
 *     <li>简单命令执行（仅获取退出码）</li>
 *     <li>捕获标准输出 / 标准错误到文件或字符串</li>
 *     <li>自定义工作目录、环境变量</li>
 *     <li>超时控制</li>
 *     <li>异步执行</li>
 *     <li>逐行回调处理输出</li>
 * </ul>
 *
 * @author admin
 */
public class ShellUtil {

    /** 默认字符集 */
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    // ======================== 私有构造 ========================

    private ShellUtil() {
        // 工具类禁止实例化
    }

    // ======================== 简便入口（单条命令字符串） ========================

    /**
     * 执行单条命令（不捕获输出）。
     *
     * @param commandLine 完整命令行字符串，例如 {@code "ls -la /tmp"}
     * @return 进程退出码
     */
    public static int run(String commandLine) {
        CommandLine cmd = CommandLine.parse(commandLine);
        return executeInternal(cmd, Collections.emptyMap(), null, null, null, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    /**
     * 执行单条命令，将标准输出和标准错误写入指定文件。
     *
     * @param commandLine 完整命令行字符串
     * @param out         标准输出文件（可为 null）
     * @param error       标准错误文件（可为 null）
     * @return 进程退出码
     */
    public static int run(String commandLine, File out, File error) {
        CommandLine cmd = CommandLine.parse(commandLine);
        return executeInternal(cmd, Collections.emptyMap(), null, out, error, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    /**
     * 执行命令（不捕获输出）。
     *
     * @param executable 可执行程序
     * @param commands   参数列表
     * @return 进程退出码
     */
    public static int run(String executable, List<String> commands) {
        return run(executable, commands, (File) null, null);
    }



    /**
     * 执行命令，将标准输出和标准错误写入指定文件。
     *
     * @param executable 可执行程序
     * @param commands   参数列表
     * @param out        标准输出文件（可为 null）
     * @param error      标准错误文件（可为 null）
     * @return 进程退出码
     */
    public static int run(String executable, List<String> commands, File out, File error) {
        return run(executable, commands, Collections.emptyMap(), out, error);
    }

    /**
     * 执行命令，支持自定义环境变量，将标准输出和标准错误写入指定文件。
     *
     * @param executable   可执行程序
     * @param commands     参数列表
     * @param environments 环境变量（可为空 Map）
     * @param out          标准输出文件（可为 null）
     * @param error        标准错误文件（可为 null）
     * @return 进程退出码
     */
    public static int run(String executable, List<String> commands, Map<String, String> environments,
                          File out, File error) {
        return run(executable, commands, environments, null, out, error, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    /**
     * 执行命令（最完整的重载），支持环境变量、工作目录、输出文件和超时。
     *
     * @param executable   可执行程序
     * @param commands     参数列表
     * @param environments 环境变量
     * @param workDir      工作目录（可为 null，使用当前目录）
     * @param out          标准输出文件（可为 null）
     * @param error        标准错误文件（可为 null）
     * @param timeoutMs    超时时间（毫秒），{@link ExecuteWatchdog#INFINITE_TIMEOUT} 表示不超时
     * @return 进程退出码
     */
    public static int run(String executable, List<String> commands, Map<String, String> environments,
                          File workDir, File out, File error, long timeoutMs) {
        List<MyCommand> myCommands = commands.stream()
                .map(t -> new MyCommand(t, true))
                .collect(Collectors.toList());
        CommandLine commandLine = buildCommandLine(executable, myCommands);
        return executeInternal(commandLine, environments, workDir, out, error, timeoutMs);
    }

    // ======================== executable + MyCommand 列表 ========================

    /**
     * 执行命令，使用 {@link MyCommand} 参数列表（可精确控制引号处理）。
     *
     * @param executable   可执行程序
     * @param commands     {@link MyCommand} 参数列表
     * @param environments 环境变量
     * @param out          标准输出文件（可为 null）
     * @param error        标准错误文件（可为 null）
     * @return 进程退出码
     */
    public static int run(String executable, List<MyCommand> commands, Map<String, String> environments,
                          File out, File error, @SuppressWarnings("unused") boolean useMyCommand) {
        CommandLine commandLine = buildCommandLine(executable, commands);
        return executeInternal(commandLine, environments, null, out, error, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    // ======================== 捕获输出到字符串 ========================

    /**
     * 执行单条命令并捕获标准输出为字符串，如果返回值不为0则抛出runtime异常
     *
     * @param commandLine 完整命令行字符串
     * @return 标准输出内容
     */
    public static String runAndCapture(String commandLine) {
        return runAndCapture(commandLine, DEFAULT_CHARSET);
    }

    /**
     * 执行单条命令并按指定字符集捕获标准输出为字符串。
     *
     * @param commandLine 完整命令行字符串
     * @param charset     输出字符集
     * @return 标准输出内容
     */
    public static String runAndCapture(String commandLine, Charset charset) {
        CommandLine cmd = CommandLine.parse(commandLine);
        return captureInternal(cmd, Collections.emptyMap(), null, charset, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    /**
     * 执行命令并捕获标准输出为字符串。
     *
     * @param executable 可执行程序
     * @param commands   参数列表
     * @return 标准输出内容
     */
    public static String runAndCapture(String executable, List<String> commands) {
        return runAndCapture(executable, commands, DEFAULT_CHARSET);
    }

    /**
     * 执行命令并按指定字符集捕获标准输出为字符串。
     *
     * @param executable 可执行程序
     * @param commands   参数列表
     * @param charset    输出字符集
     * @return 标准输出内容
     */
    public static String runAndCapture(String executable, List<String> commands, Charset charset) {
        List<MyCommand> myCommands = commands.stream()
                .map(t -> new MyCommand(t, true))
                .collect(Collectors.toList());
        CommandLine commandLine = buildCommandLine(executable, myCommands);
        return captureInternal(commandLine, Collections.emptyMap(), null, charset, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    /**
     * 执行命令并捕获标准输出为字符串（完整版）。
     *
     * @param executable   可执行程序
     * @param commands     参数列表
     * @param environments 环境变量
     * @param workDir      工作目录（可为 null）
     * @param charset      输出字符集
     * @param timeoutMs    超时时间（毫秒）
     * @return 标准输出内容
     */
    public static String runAndCapture(String executable, List<String> commands,
                                       Map<String, String> environments, File workDir,
                                       Charset charset, long timeoutMs) {
        List<MyCommand> myCommands = commands.stream()
                .map(t -> new MyCommand(t, true))
                .collect(Collectors.toList());
        CommandLine commandLine = buildCommandLine(executable, myCommands);
        return captureInternal(commandLine, environments, workDir, charset, timeoutMs);
    }

    // ======================== 逐行回调 ========================

    /**
     * 执行命令并逐行处理输出。
     *
     * @param commandLine 完整命令行字符串
     * @param callback    逐行回调
     * @return 进程退出码
     */
    public static int runWithCallback(String commandLine, LineCallback callback) {
        CommandLine cmd = CommandLine.parse(commandLine);
        return callbackInternal(cmd, Collections.emptyMap(), null, callback, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    /**
     * 执行命令并逐行处理输出。
     *
     * @param executable 可执行程序
     * @param commands   参数列表
     * @param callback   逐行回调
     * @return 进程退出码
     */
    public static int runWithCallback(String executable, List<String> commands, LineCallback callback) {
        List<MyCommand> myCommands = commands.stream()
                .map(t -> new MyCommand(t, true))
                .collect(Collectors.toList());
        CommandLine commandLine = buildCommandLine(executable, myCommands);
        return callbackInternal(commandLine, Collections.emptyMap(), null, callback, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    /**
     * 执行命令并逐行处理输出（完整版）。
     *
     * @param executable   可执行程序
     * @param commands     参数列表
     * @param environments 环境变量
     * @param workDir      工作目录（可为 null）
     * @param callback     逐行回调
     * @param timeoutMs    超时时间（毫秒）
     * @return 进程退出码
     */
    public static int runWithCallback(String executable, List<String> commands,
                                      Map<String, String> environments, File workDir,
                                      LineCallback callback, long timeoutMs) {
        List<MyCommand> myCommands = commands.stream()
                .map(t -> new MyCommand(t, true))
                .collect(Collectors.toList());
        CommandLine commandLine = buildCommandLine(executable, myCommands);
        return callbackInternal(commandLine, environments, workDir, callback, timeoutMs);
    }

    // ======================== 异步执行 ========================

    /**
     * 异步执行单条命令，返回 {@link CompletableFuture}。
     *
     * @param commandLine 完整命令行字符串
     * @return 包含进程退出码的 CompletableFuture
     */
    public static CompletableFuture<Integer> runAsync(String commandLine) {
        return CompletableFuture.supplyAsync(() -> run(commandLine));
    }

    /**
     * 异步执行命令，返回 {@link CompletableFuture}。
     *
     * @param executable 可执行程序
     * @param commands   参数列表
     * @return 包含进程退出码的 CompletableFuture
     */
    public static CompletableFuture<Integer> runAsync(String executable, List<String> commands) {
        return CompletableFuture.supplyAsync(() -> run(executable, commands, (File) null, null));
    }

    /**
     * 异步执行单条命令并捕获输出，返回 {@link CompletableFuture}。
     *
     * @param commandLine 完整命令行字符串
     * @return 包含标准输出内容的 CompletableFuture
     */
    public static CompletableFuture<String> runAndCaptureAsync(String commandLine) {
        return CompletableFuture.supplyAsync(() -> runAndCapture(commandLine));
    }

    // ======================== 超时控制 ========================

    /**
     * 执行单条命令，附带超时控制。
     *
     * @param commandLine 完整命令行字符串
     * @param timeoutMs   超时时间（毫秒）
     * @return 进程退出码
     */
    public static int runWithTimeout(String commandLine, long timeoutMs) {
        CommandLine cmd = CommandLine.parse(commandLine);
        return executeInternal(cmd, Collections.emptyMap(), null, null, null, timeoutMs);
    }

    /**
     * 执行命令，附带超时控制。
     *
     * @param executable 可执行程序
     * @param commands   参数列表
     * @param timeoutMs  超时时间（毫秒）
     * @return 进程退出码
     */
    public static int runWithTimeout(String executable, List<String> commands, long timeoutMs) {
        return run(executable, commands, Collections.emptyMap(), null, null, null, timeoutMs);
    }

    // ======================== 工作目录 ========================

    /**
     * 在指定工作目录下执行单条命令。
     *
     * @param commandLine 完整命令行字符串
     * @param workDir     工作目录
     * @return 进程退出码
     */
    public static int runInDir(String commandLine, File workDir) {
        CommandLine cmd = CommandLine.parse(commandLine);
        return executeInternal(cmd, Collections.emptyMap(), workDir, null, null, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    /**
     * 在指定工作目录下执行命令。
     *
     * @param executable 可执行程序
     * @param commands   参数列表
     * @param workDir    工作目录
     * @return 进程退出码
     */
    public static int runInDir(String executable, List<String> commands, File workDir) {
        return run(executable, commands, Collections.emptyMap(), workDir, null, null, ExecuteWatchdog.INFINITE_TIMEOUT);
    }

    // ======================== 回调接口 ========================

    /**
     * 逐行输出回调接口。
     */
    @FunctionalInterface
    public interface LineCallback {
        /**
         * 处理一行输出。
         *
         * @param line 输出行内容
         */
        void handle(String line);
    }

    // ======================== 内部实现 ========================

    /**
     * 构建 CommandLine 对象。
     */
    private static CommandLine buildCommandLine(String executable, List<MyCommand> commands) {
        CommandLine commandLine = new CommandLine(executable);
        for (MyCommand command : commands) {
            commandLine.addArgument(command.getCommand(), command.isQuota());
        }
        return commandLine;
    }

    /**
     * 确保文件及其父目录存在。
     */
    private static void ensureFileExists(File file) throws IOException {
        if (file != null && !file.exists()) {
            FileUtils.touch(file);
        }
    }

    /**
     * 核心执行方法：将输出写入文件。
     */
    private static int executeInternal(CommandLine commandLine, Map<String, String> environments,
                                       File workDir, File out, File error, long timeoutMs) {
        try {
            ensureFileExists(out);
            ensureFileExists(error);

            try (FileOutputStream outStream = out != null ? new FileOutputStream(out) : null;
                 FileOutputStream errorStream = error != null ? new FileOutputStream(error) : null) {

                DefaultExecutor exec = new DefaultExecutor();
                if (workDir != null) {
                    exec.setWorkingDirectory(workDir);
                }
                if (timeoutMs != ExecuteWatchdog.INFINITE_TIMEOUT) {
                    exec.setWatchdog(new ExecuteWatchdog(timeoutMs));
                }
                if (outStream != null || errorStream != null) {
                    exec.setStreamHandler(new PumpStreamHandler(outStream, errorStream));
                }

                DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
                exec.execute(commandLine, environments, handler);
                handler.waitFor();
                return handler.getExitValue();
            }
        } catch (Exception e) {
            throw new RuntimeException("Shell command execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * 核心执行方法：捕获输出到字符串。
     */
    private static String captureInternal(CommandLine commandLine, Map<String, String> environments,
                                          File workDir, Charset charset, long timeoutMs) {
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream();
             ByteArrayOutputStream errorStream = new ByteArrayOutputStream()) {

            DefaultExecutor exec = new DefaultExecutor();
            if (workDir != null) {
                exec.setWorkingDirectory(workDir);
            }
            if (timeoutMs != ExecuteWatchdog.INFINITE_TIMEOUT) {
                exec.setWatchdog(new ExecuteWatchdog(timeoutMs));
            }
            exec.setStreamHandler(new PumpStreamHandler(outStream, errorStream));

            DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
            exec.execute(commandLine, environments, handler);
            handler.waitFor();

            int exitValue = handler.getExitValue();
            if (exitValue != 0) {
                String errorOutput = errorStream.toString(charset.name());
                throw new RuntimeException("Command exited with code " + exitValue
                        + (errorOutput.isEmpty() ? "" : ": " + errorOutput));
            }

            return outStream.toString(charset.name());
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Shell command execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * 核心执行方法：逐行回调处理输出。
     */
    private static int callbackInternal(CommandLine commandLine, Map<String, String> environments,
                                        File workDir, LineCallback callback, long timeoutMs) {
        try (PipedOutputStream pos = new PipedOutputStream();
             PipedInputStream pis = new PipedInputStream(pos);
             BufferedReader reader = new BufferedReader(new InputStreamReader(pis, DEFAULT_CHARSET))) {

            DefaultExecutor exec = new DefaultExecutor();
            if (workDir != null) {
                exec.setWorkingDirectory(workDir);
            }
            if (timeoutMs != ExecuteWatchdog.INFINITE_TIMEOUT) {
                exec.setWatchdog(new ExecuteWatchdog(timeoutMs));
            }
            exec.setStreamHandler(new PumpStreamHandler(pos, pos));

            DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
            exec.execute(commandLine, environments, handler);

            String line;
            while ((line = reader.readLine()) != null) {
                callback.handle(line);
            }

            handler.waitFor();
            return handler.getExitValue();
        } catch (Exception e) {
            throw new RuntimeException("Shell command execution failed: " + e.getMessage(), e);
        }
    }
}
