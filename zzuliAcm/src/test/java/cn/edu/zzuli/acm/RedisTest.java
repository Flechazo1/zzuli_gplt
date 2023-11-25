package cn.edu.zzuli.acm;

import cn.edu.zzuli.acm.entity.User;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;

@SpringBootTest
public class RedisTest {


    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    @Test
    void testRedis() {
        redisTemplate.opsForValue().set("acm", "yes");

        System.out.println(redisTemplate.opsForValue().get("acm"));

        User user = new User();
        user.setUsername("hello");
        user.setNickname("suzy");
        user.setAddTime(LocalDateTime.now());

//        String o = (String) JSONObject.toJSON(user);
        redisTemplate.opsForValue().set("u", user);
        System.out.println(redisTemplate.opsForValue().get("u"));

    }

}
