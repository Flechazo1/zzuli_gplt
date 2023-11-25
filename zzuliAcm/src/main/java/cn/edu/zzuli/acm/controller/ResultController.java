package cn.edu.zzuli.acm.controller;


import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.entity.Result;
import cn.edu.zzuli.acm.service.ResultService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author geji
 * @since 2020-12-21
 */
@RestController
@RequestMapping("/result")
public class ResultController {

    @Autowired
    ResultService resultService;


    @PostMapping(value = {"/info/{id}", "/info"})
    @ApiOperation("保存当前比赛的 排行榜信息，stands信息，以及题目集信息," +
            "如果是/info,那么是添加并且保存当前比赛，如果是/info/1 是保存当前结果到 id为1的比赛。")
    public R save(@PathVariable(value = "id", required = false) Integer id) {
        if (resultService.saveInfo(id)){
            return R.success().appendMsg("保存成功");
        }
        return R.error().appendMsg("保存失败");
    }


    @PostMapping("/completion")
    @ApiOperation("新创建一个比赛, 接收json格式，rankName, isIcpc, rankProblemId三者不能为null")
    public R newCompletion(@RequestBody Result result) {
        if (!StringUtils.isEmpty(result.getRankName())
                && !StringUtils.isEmpty(result.getSessionId())
                && !StringUtils.isEmpty(result.getIsIcpc())
                && !StringUtils.isEmpty(result.getRankProblemId())){

            return resultService.saveCompletionBasicInfo(result);

        }

        return R.error().appendMsg("参数有误，rankName, isIcpc, rankProblemId三者不能为null");
    }


    @GetMapping("/info/{id}")
    @ApiOperation("获取当前 id 的比赛的详细数据，比如排行榜数据")
    public R getInfoById(@PathVariable("id") Integer id) {
        return resultService.getBasicInfoById(id);
    }

    @GetMapping("/completion")
    @ApiOperation("获取所有比赛的基本信息")
    public R listCompletion() {
        List<Result> list = resultService.list(new LambdaQueryWrapper<Result>()
                .select(Result::getId, Result::getIsIcpc,
                        Result::getRankProblemId, Result::getRankName)
        );

        System.out.println(list.get(list.size()-1));
        return R.success().add("completions", list.get(list.size()-1));
    }

}

