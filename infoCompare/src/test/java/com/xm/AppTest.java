package com.xm;

import static org.junit.Assert.assertTrue;

import com.xm.entity.Data;
import com.xm.mapper.DataMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

/**
 * Unit MyLinkList for simple App.
 */
public class AppTest 
{

    @Test
    public void test1(){
        boolean mkdir = new File("C:\\Users\\dr\\Desktop\\BC比对结果" + System.currentTimeMillis() + "\\colorTagReports").mkdirs();
    }
}
