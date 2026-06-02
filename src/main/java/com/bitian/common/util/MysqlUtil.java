package com.bitian.common.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL 备份工具类。
 * <p>
 * 通过调用 {@code mysqldump} 命令完成数据库备份，
 * 底层委托 {@link ShellUtil} 执行 Shell 命令。
 *
 * @author admin
 */
public class MysqlUtil {

    /**
     * 使用默认参数备份 MySQL 数据库（默认字符集 utf8）。
     *
     * @param file     备份 SQL 文件输出路径
     * @param host     数据库主机
     * @param port     数据库端口
     * @param database 数据库名称
     * @param username 用户名
     * @param password 密码
     * @return 错误信息（为空字符串表示成功）
     */
    public static String backup(File file, String host, int port, String database,
                                String username, String password) {
        return backup(file, host, port, database, username, password, "utf8");
    }

    /**
     * 使用默认参数备份 MySQL 数据库。
     *
     * @param file           备份 SQL 文件输出路径
     * @param host           数据库主机
     * @param port           数据库端口
     * @param database       数据库名称
     * @param username       用户名
     * @param password       密码
     * @param defaultCharset 默认字符集
     * @return 错误信息（为空字符串表示成功）
     */
    public static String backup(File file, String host, int port, String database,
                                String username, String password, String defaultCharset) {
        List<String> args = new ArrayList<>();
        args.add("--ssl-mode=DISABLED");
        args.add("--skip-lock-tables");
        args.add("--routines");
        args.add("--add-drop-table");
        args.add("--disable-keys");
        args.add("--extended-insert");
        args.add("--default-character-set=" + defaultCharset);
        return backup(file, args, host, port, database, username, password);
    }

    /**
     * 使用自定义参数备份 MySQL 数据库。
     *
     * @param file     备份 SQL 文件输出路径
     * @param args     mysqldump 额外参数列表
     * @param host     数据库主机
     * @param port     数据库端口
     * @param database 数据库名称
     * @param username 用户名
     * @param password 密码
     * @return 错误信息（为空字符串表示成功）
     */
    public static String backup(File file, List<String> args, String host, int port,
                                String database, String username, String password) {
        // 确保输出目录存在
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        // 构建 mysqldump 命令参数
        List<String> commands = new ArrayList<>();
        commands.add("-h");
        commands.add(host);
        commands.add("-P");
        commands.add(String.valueOf(port));
        commands.add("-u");
        commands.add(username);
        commands.add("-p" + password);
        commands.addAll(args);
        commands.add(database);

        // 使用临时文件捕获错误输出
        File errorFile = null;
        try {
            errorFile = File.createTempFile("mysqldump_err_", ".log");
            int exitCode = ShellUtil.run("mysqldump", commands, file, errorFile);
            if (exitCode != 0) {
                return new String(Files.readAllBytes(errorFile.toPath()), StandardCharsets.UTF_8);
            }
            return "";
        } catch (IOException e) {
            return e.getMessage();
        } finally {
            if (errorFile != null && errorFile.exists()) {
                errorFile.delete();
            }
        }
    }
}
