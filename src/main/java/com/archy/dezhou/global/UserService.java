package com.archy.dezhou.global;


import com.archy.dezhou.dao.UserMapper;
import com.archy.dezhou.entity.User;
import org.apache.log4j.Logger;

import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class UserService {

    protected static Logger log = Logger.getLogger(UserService.class);

    private static SqlSessionFactory sqlMapper = null;

    static{
        try {
            Reader reader = Resources.getResourceAsReader("mybatis.xml");
            sqlMapper = new SqlSessionFactoryBuilder().build(reader);
        }
        catch (Exception ex){
            log.error("err",ex);
        }
    }


    public static void test(){

        SqlSession session = sqlMapper.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);

        User user = userMapper.selectByAccount("archy");

        session.close();

        log.info(user);

    }

    public static User autoReg(String account){

        User user = new User();
        user.setAccount(account);
        user.setMobile("18633919531");
        user.setAllmoney(1000);

        SqlSession session = sqlMapper.openSession();
        UserMapper userMapper = session.getMapper(UserMapper.class);

        int result = userMapper.insertSelective(user);
        session.commit();
        log.info("insert result:" + result);

        user = userMapper.selectByAccount(account);

        session.close();
        return user;
    }







}
