package cn.edu.zzuli.acm.controller;


import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.service.UserService;
import cn.edu.zzuli.acm.vo.UserRankingVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
@RestController
@RequestMapping("/user")
@Api("user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getUserScore/{page}")
    @ApiOperation("获取到用户的分数")
    public R getUserScore(@PathVariable("page") Integer page){
        UserRankingVo to = userService.getUserScore(page, 50);
        return R.success().add("ranking", to);
    }
}

