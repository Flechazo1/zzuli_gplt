package cn.edu.zzuli.acm.job;

import cn.edu.zzuli.acm.entity.Config;
import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.enums.InfoConst;
import cn.edu.zzuli.acm.enums.JobConst;
import cn.edu.zzuli.acm.mapper.ConfigMapper;
import cn.edu.zzuli.acm.util.PythonExecuteUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InitBeforeAppStartRunner implements CommandLineRunner {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ConfigMapper configMapper;

    @Override
    public void run(String... args) throws Exception {
        //初始化一些信息
        initInfo();
        //打印题目集信息
        Object problems_set
                = redisTemplate.opsForValue().get(InfoConst.PROBLEM_SET.KEY());
        log.info("当前 problems_set 情况 --->>> " + problems_set);

        //初始化 session 和 problemid
        Config config = configMapper.selectById(1);
        PythonExecuteUtils.updateTheSessionId(config.getSession());
        PythonExecuteUtils.updateTheProblemsId(config.getProblemid());
        log.info("当前info 情况 --->>> session : " + config.getSession() + ", problem_id: " + config.getProblemid());
    }

    private void initInfo() {
        //判断比赛是不是已经开始了
        if (!redisTemplate.hasKey(InfoConst.IS_START.KEY())) {
            //默认关闭
            redisTemplate.opsForValue().set(InfoConst.IS_START.KEY(), false);
            log.info(">>> 比赛处于关闭中...");
        }else {
            boolean isStart = (boolean) redisTemplate.opsForValue().get(InfoConst.IS_START.KEY());
            if (isStart) log.info("<<< 比赛正在进行中...");
            else log.info(">>> 比赛处于关闭中...");
        }

        log.info("天梯赛比赛Stands初始化：个人初级分限制：0，进阶分限制为：0");
        if (!redisTemplate.hasKey(InfoConst.STANDS.KEY())) {
            Stands stands = new Stands(1, 0.0, 0.0);
            redisTemplate.opsForValue().set(InfoConst.STANDS.KEY(), stands);
        }

        //默认不是ICPC赛制
        if (!redisTemplate.hasKey(InfoConst.IS_ICPC.KEY())) {
            redisTemplate.opsForValue().set(InfoConst.IS_ICPC.KEY(), false);
            log.info("当前赛制为：天梯赛");
        }else {
            Boolean isICPC = (Boolean) redisTemplate.opsForValue().get(InfoConst.IS_ICPC.KEY());
            if (isICPC) {
                log.info("当前赛制为：ICPC赛制");
            }else {
                log.info("当前赛制为：天梯赛");
            }
        }

        //对定时任务标识进行初始化
        //默认设置三个定时任务为关闭状态
        redisTemplate.opsForValue().set(JobConst.SCORE_JOB.KEY(), false);
        redisTemplate.opsForValue().set(JobConst.PROBLEM_JOB.KEY(), false);
        redisTemplate.opsForValue().set(JobConst.ICPCRANK_JOB.KEY(), false);
    }
}
