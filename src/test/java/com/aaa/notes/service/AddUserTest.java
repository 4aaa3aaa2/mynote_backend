package com.aaa.notes.service;


import org.junit.Test;
import org.junit.runner.RunWith;
import com.aaa.notes.mapper.UserMapper;
import com.aaa.notes.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class AddUserTest {
    @Autowired
    private UserMapper userMapper;

    @Test
    public void addTestCount(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        for (int i = 1;i<2; i++){
            User user = new User();
            user.setAccount("account"+ String.format("%02d",i));
            user.setUserName("username"+ String.format("%02d", i));
            user.setPassword(passwordEncoder.encode("password" + String.format("%02d", i)));

            userMapper.insert(user);

        }
    }

}
