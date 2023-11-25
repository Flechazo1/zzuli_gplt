package cn.edu.zzuli.acm.job;

import cn.edu.zzuli.acm.service.RankJsonService;
import cn.edu.zzuli.acm.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetProblemSubmitRunner implements Runnable{

    @Autowired
    RankJsonService rankJsonService;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void run() {
        //上次比赛的时候，有的数据没有爬到，也不知道为什么，为了防止中间缺少数据，我在这里加一个同步锁试试
        synchronized (GetProblemSubmitRunner.class) {
//            LocalDateTime start = LocalDateTime.now();
//            执行脚本去爬取分数，然后将当前最新的 json数据存入到redis中
            rankJsonService.getRankJsonInfo();
//            LocalDateTime end = LocalDateTime.now();
//            Duration duration = Duration.between(start, end);
//            System.out.println(duration.toMillis());
        }
    }
}
