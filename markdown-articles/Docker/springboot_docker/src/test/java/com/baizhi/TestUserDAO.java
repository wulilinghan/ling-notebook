package com.baizhi;

import com.baizhi.dao.UserDAO;
import com.baizhi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class TestUserDAO {

    @Autowired
    private UserDAO userDAO;


    @Test
    public void save(){
        userDAO.save(new User(null,"小陈",new Date()));
    }

    @Test
    public void findAll(){
        userDAO.findAll().forEach(user-> System.out.println("user = " + user));
    }
}
