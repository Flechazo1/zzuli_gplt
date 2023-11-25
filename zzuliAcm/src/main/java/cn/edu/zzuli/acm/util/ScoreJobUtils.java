package cn.edu.zzuli.acm.util;

import cn.edu.zzuli.acm.enums.JobConst;
import cn.edu.zzuli.acm.job.GetUserScoreRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
public class ScoreJobUtils {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private GetUserScoreRunner userScoreRunner;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Value("${pythonjob.score}")
    private String step;

    private ScheduledFuture<?> future;

    public boolean openOrStopJob(){
        System.out.println("start job: get user-score-rank");
        if(future == null || future.isCancelled()){
            future = threadPoolTaskScheduler.schedule(userScoreRunner,
                    triggerContext -> new CronTrigger(step).nextExecutionTime(triggerContext));
            redisTemplate.opsForValue().set(JobConst.SCORE_JOB.KEY(), true);
            return true;
        }

        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            redisTemplate.opsForValue().set(JobConst.SCORE_JOB.KEY(), false);
            System.out.println("user-score-rank job --> stop");
        }

        return false;
    }

    /**
     * 关闭定时任务
     * @return
     */
    public boolean stopTheJob() {
        if (future == null) {
            return false;
        }

        future.cancel(true);
        return true;
    }
}
