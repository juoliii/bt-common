package com.bitian.testmaven;

import com.bitian.common.util.LambdaUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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

    public static void main(String[] args) {
//        System.out.println(ResultJson.mapData().put("wef",12));
        List<String> list=new ArrayList<>();
        list.add("12");
        list.add("23");
        list.stream().map(LambdaUtil.wrap(item->test11(item))).collect(Collectors.toList());

    }
}
