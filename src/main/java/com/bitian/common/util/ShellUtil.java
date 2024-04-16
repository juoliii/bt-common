package com.bitian.common.util;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecuteResultHandler;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author admin
 */
public class ShellUtil {

    public static int run(String executable, List<String> commands, String out, String error) throws Exception {
        String[] array = commands.toArray(new String[commands.size()]);
        File outFile = null,errorFile=null;
        if(StringUtils.isNotBlank(out)){
            outFile=new File(out);
        }
        if(StringUtils.isNotBlank(error)){
            errorFile=new File(error);
        }
        return run(executable,array,outFile,errorFile);
    }

    public static int run(String executable, List<String> commands, File out, File error) throws Exception {
        String[] array = commands.toArray(new String[commands.size()]);
        return run(executable,array,out,error);
    }

    public static int run(String executable, String[] commands, File out,File error) throws Exception {
        FileOutputStream outStream=null;
        if(out!=null){
            if(out.exists()) FileUtils.touch(out);
            outStream=new FileOutputStream(out);
        }
        FileOutputStream errorStream=null;
        if(error!=null){
            if(!error.exists())FileUtils.touch(error);
            errorStream=new FileOutputStream(error);
        }
        try{
            DefaultExecuteResultHandler handler=new DefaultExecuteResultHandler();
            CommandLine commandLine =new CommandLine(executable);
            commandLine.addArguments(commands);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setStreamHandler(new PumpStreamHandler(outStream,errorStream));
            exec.execute(commandLine,handler);
            handler.waitFor();
            return handler.getExitValue();
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }finally {
            try{
                IOUtils.closeQuietly(outStream);
                IOUtils.closeQuietly(errorStream);
            }catch (Exception e){}
        }
    }

    @Deprecated
    public static int execute(String executable, String[] commands, StreamLineCallback callback) throws Exception {
        PipedOutputStream pos=new PipedOutputStream();
        PipedInputStream pis=new PipedInputStream();
        BufferedReader br=null;
        try{
            DefaultExecuteResultHandler handler=new DefaultExecuteResultHandler();
            CommandLine commandLine =new CommandLine(executable);
            commandLine.addArguments(commands);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setStreamHandler(new PumpStreamHandler(pos,pos));
            exec.execute(commandLine,handler);
            pos.connect(pis);
            br=new BufferedReader(new InputStreamReader(pis));
            String str;
            while ((str=br.readLine())!=null){
                callback.handle(str);
            }
            handler.waitFor();
            return handler.getExitValue();
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

    @Deprecated
    public static int execute(String[] commands,StreamLineCallback callback) throws Exception {
        return execute("/bin/sh",commands,callback);
    }

    @Deprecated
    @FunctionalInterface
    public interface StreamLineCallback{
        void handle(String line);
    }
}

