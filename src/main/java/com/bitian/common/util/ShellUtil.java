package com.bitian.common.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @author admin
 */
public class ShellUtil {

    public static void execute(String executable,String[] commands,StreamLineCallback callback) throws Exception {
        PipedOutputStream pos=new PipedOutputStream();
        PipedInputStream pis=new PipedInputStream();
        BufferedReader br=null;
        try{
            DefaultExecuteResultHandler handler=new DefaultExecuteResultHandler();
            CommandLine commandLine =new CommandLine(executable);
            commandLine.addArguments(commands);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setStreamHandler(new PumpStreamHandler(pos));
            exec.execute(commandLine,handler);
            pos.connect(pis);
            br=new BufferedReader(new InputStreamReader(pis));
            String str;
            while ((str=br.readLine())!=null){
                callback.handle(str);
            }
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }finally {
            try{
                IOUtils.closeQuietly(br);
                IOUtils.closeQuietly(pos);
                IOUtils.closeQuietly(pis);
            }catch (Exception e){}
        }
    }

    public static void execute(String[] commands,StreamLineCallback callback) throws Exception {
        execute("/bin/sh",commands,callback);
    }

    @FunctionalInterface
    public interface StreamLineCallback{
        void handle(String line);
    }
}

