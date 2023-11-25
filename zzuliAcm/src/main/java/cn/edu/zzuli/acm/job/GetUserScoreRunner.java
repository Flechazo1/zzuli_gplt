package cn.edu.zzuli.acm.job;

import cn.edu.zzuli.acm.service.RankJsonService;
import cn.edu.zzuli.acm.service.StandsService;
import cn.edu.zzuli.acm.util.PythonExecuteUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GetUserScoreRunner implements Runnable{

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    @Autowired
//    private SumScoreUtils sumScoreUtils;

    @Autowired
    private StandsService standsService;

    @Autowired
    RankJsonService rankJsonService;

    @Override
    public void run() {
//        LocalDateTime start = LocalDateTime.now();
        log.info("开始计算成绩");
        PythonExecuteUtils.doRankingPython(null);
        redisTemplate.opsForValue().set("userRanking", rankJsonService.getRankingFromRedis());
        redisTemplate.opsForValue().set("teamUpdateFlag" ,true);
        log.info("成绩计算完成");
//        LocalDateTime end = LocalDateTime.now();
//        Duration duration = Duration.between(start, end);
//        System.out.println(duration.toMillis());

        //时代变了大人。
//        log.info("开始计算成绩");
//        //计算成绩的定时任务，每次计算真的有点憨憨
//        UserScoreTO to = new UserScoreTO();
//        boolean result = false;
//        if(redisTemplate.opsForValue().get("scoreUpdateFlag") == null){
//            log.info("尚未完成系统初始化");
//        }else {
//            result = (boolean) redisTemplate.opsForValue().get("scoreUpdateFlag");
//        }
//
//        if(result){
//            //此时需要重新计算结果
//            List<UserScore> list = sumScoreUtils.getVirUserScore();
//            Collections.sort(list, new UserScoreComparator());
//            //Collections.sort(list, Comparator.comparing());
//            //封装个人成绩
//            to.setUserScores(list);
//            Stands stands = standsService.getStands();
//            //封装各个分段的限制分数
////            BeanUtils.copyProperties(stands, to);
//            redisTemplate.opsForValue().set("singleScoreTO", to);
//            //自己已经取过数据，将redis中scoreUpdateFlag置为false
//            redisTemplate.opsForValue().set("scoreUpdateFlag", false);
//            //团队可以更新数据了...
//            redisTemplate.opsForValue().set("teamUpdateFlag", true);
//        }
//        log.info("成绩计算完成");
    }
}
