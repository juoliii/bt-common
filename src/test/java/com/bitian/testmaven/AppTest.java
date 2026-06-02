package com.bitian.testmaven;

import com.bitian.common.dto.BaseForm;
import com.bitian.common.dto.User;
import com.bitian.common.util.PrimaryKeyUtil;
import com.bitian.common.util.SystemInfo;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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


    public static void main(String[] args) throws Exception {
        System.out.println(SystemInfo.getInstance().getNetworkInterfaces());
    }
}
