package cn.edu.zzuli.acm.controller;


import cn.edu.zzuli.acm.entity.Config;
import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.enums.ResCodeConst;
import cn.edu.zzuli.acm.service.ConfigService;
import cn.edu.zzuli.acm.service.RankJsonService;
import cn.edu.zzuli.acm.service.StandsService;
import cn.edu.zzuli.acm.util.ICPCRankJobUtils;
import cn.edu.zzuli.acm.util.ProblemSubmitJobUtils;
import cn.edu.zzuli.acm.util.ScoreJobUtils;
import cn.edu.zzuli.acm.vo.ConfigVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author geji
 * @since 2020-11-30
 */
@RestController
@RequestMapping("/config")
@Api(tags = "配置api (后台用)")
public class ConfigController {

    @Autowired
    private ConfigService configService;

    @Autowired
    private RankJsonService rankJsonService;

    @Autowired
    StandsService standsService;

    @Autowired
    public ProblemSubmitJobUtils problemSubmitJobUtils;

    @Autowired
    private ScoreJobUtils scoreJobUtils;

    @Autowired
    private ICPCRankJobUtils icpcRankJobUtils;

    @GetMapping("/info")
    public R initTheInfo() {
        ConfigVo adminInfo = configService.getAdminInfo();
        return R.success().add("initInfo", adminInfo);
    }

    @PostMapping("/info/update")
    @ApiOperation("修改全局的 session 和 problemsid, 修改后会自动去执行py脚本爬取题目集信息")
    public R updateAdminConfig(@RequestBody Config config){
        //数据库就一条数据，为什么放到数据库呢...
        config.setId(1);
        boolean result = configService.updateAdminConfig(config);
        if(result){
            return R.success().appendMsg("init success...");
        }else{
            return R.error();
        }
    }

    @GetMapping("/problems/init/{problemid}")
    @ApiOperation("开启 py 脚本去爬 对应的 题目集的 序号和分数")
    public R problesInit(@PathVariable String problemid) {
        boolean b = configService.initRedisInfo(problemid);
        if (b) return R.success().appendMsg("init success...");
        return R.error().appendMsg("好像哪里出错啦...");
    }

    @PostMapping("/stands/update")
    @ApiOperation("修改基础，进阶和高阶，三个档位的标准划分值")
    public R updateStands(@RequestBody Stands stands){
        boolean result = standsService.updateStands(stands);
        if(result){
            return R.success();
        }else{
            return R.error();
        }
    }

    @GetMapping("/job/problems")
    @ApiOperation("开启爬取数据的定时任务")
    public R startTheJob(){
        boolean b = problemSubmitJobUtils.openOrStopJob();
        if (b)
            return R.success().setCode(ResCodeConst.JOB_START.CODE()).appendMsg("爬取题目提交记录的定时任务已开启");
        else
            return R.success().setCode(ResCodeConst.JOB_STOP.CODE()).appendMsg("已关闭定时任务");
    }

    @GetMapping("/job/rank")
    @ApiOperation("开启 或 关闭计算分数的定时任务")
    public R startScoreJob(){
        boolean b = scoreJobUtils.openOrStopJob();
        if (b)
            return R.success().setCode(ResCodeConst.JOB_START.CODE()).appendMsg("计算排行的定时任务已开启");
        else
            return R.success().setCode(ResCodeConst.JOB_STOP.CODE()).appendMsg("已关闭定时任务");
    }


    @GetMapping("/job/icpc/rank")
    @ApiOperation("开启 或 关闭 icpc 赛制的爬取榜单任务")
    public R icpcJobStart() {
        boolean b = icpcRankJobUtils.openOrStopJob();
        if (b)
            return R.success().setCode(ResCodeConst.JOB_START.CODE()).appendMsg("计算 ICPC 排行的定时任务已开启");
        else
            return R.success().setCode(ResCodeConst.JOB_STOP.CODE()).appendMsg("已关闭定时任务");
    }

    @GetMapping("/fixScore")
    @ApiOperation("比赛时少爬的数据，现在给他补一下")
    public R fixScore() {
        rankJsonService.fixScore();
        return R.success();
    }
}