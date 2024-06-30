package com.bitian.testmaven;

import com.bitian.common.dto.User;
import com.bitian.common.util.DesUtil;
import com.bitian.common.util.JwtUtils;
import com.bitian.common.util.LambdaUtil;
import com.bitian.common.util.ShellUtil;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.exec.*;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//        Map<String,Object> map=new HashMap<>();
//        map.put("key","value");
//        TestUser token=JwtUtils.instance().getUser("","fwef",TestUser.class);
//        System.out.println(token);
//        System.out.println(JwtUtils.instance().getClaims("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ6dCIsImV4cCI6MTcwOTg0NTA0NCwiaWF0IjoxNzA2MjQ1MDQ0LCJrZXkiOiJ2YWx1ZSIsImp0aSI6ImIwNmNhMTgwLTBkZjctNDM2MS1iOTVkLWNlZDBmZjcyMzIxYiJ9.GIgA4pwRNdYUnvRDR748aUwa-Mb__Rh61sNey-tnxog","fwefwef"));
//        System.out.println(ResultJson.mapData().put("wef",12));
//        List<String> list=new ArrayList<>();
//        list.add("12");
//        list.add("23");
//        list.stream().map(LambdaUtil.wrap(item->test11(item))).collect(Collectors.toList());
//        ShellUtil.execute("java",new String[]{"-jar","D:\\workspace\\jiyinzu\\lims\\lims-api\\target\\datashare-lims-0.0.1-SNAPSHOT.jar"},line->System.out.println(line));

//        ExecuteResultHandler handler=new ExecuteResultHandler() {
//            @Override
//            public void onProcessComplete(int exitValue) {
//                System.out.println("++++++++++++++++++++++"+exitValue);
//            }
//
//            @Override
//            public void onProcessFailed(ExecuteException e) {
//                System.out.println("==========================="+e);
//            }
//        };
//        CommandLine commandLine =new CommandLine("D:/test.cmd");
//        commandLine.addArguments(new String[]{"D:/test.cmd"});
//        DefaultExecutor exec = new DefaultExecutor();
//        exec.setStreamHandler(new PumpStreamHandler());
//        exec.execute(commandLine,handler);
//        System.out.println("------------------------------------");
//        String [] commands={"/root/test.sh"};
//        File file=new File("/root/error.log");
//        File out=new File("/root/out.log");
//        int id=ShellUtil.run("/bin/sh",commands,null,null);
//        System.out.println(id);
//        DesUtil desUtil=DesUtil.getInstance("jiyinzu2024JIYINZU");
//        File root=new File("D://logs/");
//        File file=new File(root,"/");
//        System.out.println(root.getAbsolutePath());
//        System.out.println(file.getAbsolutePath());
//        System.out.println(Paths.get("/opt/wef","sdf.txt"));
        Statement statement=CCJSqlParserUtil.parse("select a.* from wef a left join test t on t.aid=a.id where a.name ='wef' and exists(select 1 from c where id=a.cid)");
        System.out.println(statement.getClass());
//        if(statement instanceof PlainSelect){
            PlainSelect select= (PlainSelect) statement;
            Expression where = select.getWhere();
            if (where != null) {
                // 如果已存在WHERE子句，添加新条件
                AndExpression and = new AndExpression(where, CCJSqlParserUtil.parseCondExpression(" or (name = 'John Doe' or b=2) and c=3"));
                select.setWhere(and);
            } else {
                // 如果不存在WHERE子句，直接设置新条件
                select.setWhere(CCJSqlParserUtil.parseCondExpression("name = 'John Doe'"));
            }
            System.out.println(select.getWhere());
//        }
    }
}
