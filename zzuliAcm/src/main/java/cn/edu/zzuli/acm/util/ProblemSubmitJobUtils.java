package cn.edu.zzuli.acm.util;

import cn.edu.zzuli.acm.enums.JobConst;
import cn.edu.zzuli.acm.job.GetProblemSubmitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
public class ProblemSubmitJobUtils {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Autowired
    private GetProblemSubmitRunner getProblemSubmitRunner;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Value("${pythonjob.problem-set}")
    private String step;

    private ScheduledFuture<?> future;


    public boolean openOrStopJob(){
        System.out.println("start job: get problem-set-submit");
        if(future == null || future.isCancelled()){
            future = threadPoolTaskScheduler.schedule(getProblemSubmitRunner,
                    triggerContext -> new CronTrigger(step).nextExecutionTime(triggerContext));
            redisTemplate.opsForValue().set(JobConst.PROBLEM_JOB.KEY(), true);
            return true;
        }

        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            redisTemplate.opsForValue().set(JobConst.PROBLEM_JOB.KEY(), false);
            System.out.println("problem-set-submit job --> stop");
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
