package com.bitian.common.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DaemonExecutor;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author admin
 */
public class MysqlUtil {

    public static String backup(File file, String host, int port, String database, String username, String password, String defaultCharset) {
        List<String> args=new ArrayList<>();
        args.add("--ssl-mode=DISABLED");
        args.add("--skip-lock-tables");
        args.add("--routines");
        args.add("--add-drop-table");
        args.add("--disable-keys");
        args.add("--extended-insert");
        args.add("--default-character-set="+defaultCharset);
        return backup(file,args,host,port,database,username,password);
    }

    public static String backup(File file, List<String> args, String host, int port, String database, String username, String password){
        FileOutputStream susStream=null;
        ByteArrayOutputStream errStream=null;
        int result=-1;
        try{
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            DefaultExecutor exec = new DaemonExecutor();
            //接收正常结果流
            susStream = new FileOutputStream(file);
            //接收异常结果流
            errStream = new ByteArrayOutputStream();
            CommandLine commandLine =new CommandLine("mysqldump");
            commandLine.addArgument("-h");
            commandLine.addArgument(host);
            commandLine.addArgument("-P");
            commandLine.addArgument(port+"");
            commandLine.addArgument("-u");
            commandLine.addArgument(username);
            commandLine.addArgument("-p"+password);
            args.forEach(item->{
                commandLine.addArguments(item);
            });
            commandLine.addArgument(database);
            PumpStreamHandler streamHandler = new PumpStreamHandler(susStream, errStream);
            exec.setStreamHandler(streamHandler);
            result=exec.execute(commandLine);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            String error="";
            if(result!=0){
                error=new String(errStream.toByteArray(), StandardCharsets.UTF_8);
            }
            IOUtils.closeQuietly(susStream);
            IOUtils.closeQuietly(errStream);
            return error;
        }
    }

}
