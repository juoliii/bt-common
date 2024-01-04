package com.bitian.testmaven;

import com.bitian.common.util.LambdaUtil;
import com.bitian.common.util.ShellUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.apache.commons.exec.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    public static String test11(String var) throws Exception{
        System.out.println(var);
        return "wef";
    }

    public static void main(String[] args) throws Exception {
//        System.out.println(ResultJson.mapData().put("wef",12));
//        List<String> list=new ArrayList<>();
//        list.add("12");
//        list.add("23");
//        list.stream().map(LambdaUtil.wrap(item->test11(item))).collect(Collectors.toList());
//        ShellUtil.execute("java",new String[]{"-jar","D:\\workspace\\jiyinzu\\lims\\lims-api\\target\\datashare-lims-0.0.1-SNAPSHOT.jar"},line->System.out.println(line));

        ExecuteResultHandler handler=new ExecuteResultHandler() {
            @Override
            public void onProcessComplete(int exitValue) {
                System.out.println("++++++++++++++++++++++"+exitValue);
            }

            @Override
            public void onProcessFailed(ExecuteException e) {
                System.out.println("==========================="+e);
            }
        };
        CommandLine commandLine =new CommandLine("D:/test.cmd");
//        commandLine.addArguments(new String[]{"D:/test.cmd"});
        DefaultExecutor exec = new DefaultExecutor();
//        exec.setStreamHandler(new PumpStreamHandler());
        exec.execute(commandLine,handler);
        System.out.println("------------------------------------");
    }
}
