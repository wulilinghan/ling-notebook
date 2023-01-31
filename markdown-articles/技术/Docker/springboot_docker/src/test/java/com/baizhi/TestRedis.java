package com.baizhi;

import com.baizhi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.Date;

@SpringBootTest
public class TestRedis {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private RedisTemplate redisTemplate;


    @Test
    public void testStringRedisTemplate(){

        String name = stringRedisTemplate.opsForValue().get("name");
        System.out.println("name = " + name);

        stringRedisTemplate.opsForValue().set("content","this is good man");

    }

    @Test
    public void testRedisTemplate(){
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        User user = new User("21", "小陈", new Date());
        redisTemplate.opsForValue().set("user",user);
        User user1 = (User) redisTemplate.opsForValue().get("user");
        System.out.println("user1 = " + user1);

    }

}
