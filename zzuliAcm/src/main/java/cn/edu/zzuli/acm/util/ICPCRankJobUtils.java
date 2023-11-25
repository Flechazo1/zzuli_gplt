package cn.edu.zzuli.acm.util;

import cn.edu.zzuli.acm.enums.JobConst;
import cn.edu.zzuli.acm.job.ICPCRankRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;

@Component
public class ICPCRankJobUtils {

    @Autowired
    private ThreadPoolTaskScheduler threadPoolTaskScheduler;

    @Value("${pythonjob.icpc-rank}")
    private String step;

    private ScheduledFuture<?> future;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private ICPCRankRunner icpcRankRunner;

    public boolean openOrStopJob(){
        if(future == null || future.isCancelled()){
            System.out.println("start job: get icpc-rank");
            future = threadPoolTaskScheduler.schedule(
                    icpcRankRunner,
                    triggerContext -> new CronTrigger(step).nextExecutionTime(triggerContext)
            );
            redisTemplate.opsForValue().set(JobConst.ICPCRANK_JOB.KEY(), true);
            return true;
        }

        if (future != null && !future.isCancelled()) {
            future.cancel(true);
            redisTemplate.opsForValue().set(JobConst.ICPCRANK_JOB.KEY(), false);
            System.out.println("icpc-rank job --> stop");
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
