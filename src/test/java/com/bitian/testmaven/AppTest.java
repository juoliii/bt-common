package com.bitian.testmaven;

import com.bitian.common.dto.BaseForm;
import com.bitian.common.dto.User;
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

    public static String test11(String var) throws Exception{
        System.out.println(var);
        return "wef";
    }

    class TestUser implements User{

        public Object getId() {
            return null;
        }

        public String getUsername() {
            return null;
        }

        public String getName() {
            return null;
        }

        public String getNickname() {
            return null;
        }

        public String getAvatar() {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
    }
}
