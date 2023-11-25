package cn.edu.zzuli.acm;

import cn.edu.zzuli.acm.entity.Config;
import cn.edu.zzuli.acm.service.impl.ConfigServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AcmApplicationTests {

    @Autowired
    ConfigServiceImpl configService;

    @Test
    void contextLoads() {

        configService.initRedisInfo("1464243200260685824");
    }

}
