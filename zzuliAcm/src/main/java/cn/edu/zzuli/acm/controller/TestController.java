package cn.edu.zzuli.acm.controller;

import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.service.RankJsonService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Api(tags = "没啥用", hidden = true)
public class TestController {

    @Autowired
    RankJsonService rankJsonService;

    @GetMapping("/ping")
    public R ping() {
        return R.success().appendMsg("pong!");
    }

}
