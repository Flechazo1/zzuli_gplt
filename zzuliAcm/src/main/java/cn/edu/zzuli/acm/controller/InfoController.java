package cn.edu.zzuli.acm.controller;


import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.enums.InfoConst;
import cn.edu.zzuli.acm.enums.RankConst;
import cn.edu.zzuli.acm.service.StandsService;
import cn.edu.zzuli.acm.vo.ProblemSetVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
@RestController
@RequestMapping("/info")
@Api
public class InfoController {

    @Autowired
    private StandsService service;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @GetMapping("/problems")
    @ApiOperation("获取当前题目集的 序号和满分")
    public R getProBlemsSet() {
        boolean isICPC = (boolean) redisTemplate.opsForValue().get(InfoConst.IS_ICPC.KEY());
        Map<String, Object> problems_set = (Map<String, Object>) redisTemplate.opsForValue().get("problems_set");
        if (problems_set == null) return R.success().appendMsg("但是当前并没爬到题目集信息哦");
        List<ProblemSetVo> problemSets = new ArrayList<>();
        AtomicBoolean isLetter = new AtomicBoolean(false);
        problems_set.forEach((k, v) -> {
            if (k.contains("-")){
                problemSets.add(
                        new ProblemSetVo(Integer.parseInt(k.split("-")[1]),k, v));
            }else {
                isLetter.set(true);
            }
        });

//        List<ProblemSetVo> collect = problemSets.stream()
//                .sorted(ProblemSetVo::getId)
//                .collect(Collectors.toList());
        if (!isLetter.get()){
            Collections.sort(problemSets, (o1, o2) -> o1.getId() - o2.getId());
        }else {
            Collections.sort(problemSets, (o1, o2) -> o1.getLabel().compareTo(o2.getLabel()));
        }
        return R.success().add("problems_set", problemSets).add("icpc", isICPC);
    }



    @GetMapping("/stands")
    @ApiOperation("获取当前三个阶段的 标准分")
    public R getNowStands(){
        Stands stands = service.getStands();
        return R.success().add("stands", stands);
    }

    @GetMapping("/firstAc")
    @ApiOperation("获取ICPC赛制中各个题目第一次AC的信息")
    public R getFirstAc() {
        return R.success().add("firstAc",
                redisTemplate.opsForValue().get(RankConst.FIRST_SUBMIT.KEY()));
    }
}

