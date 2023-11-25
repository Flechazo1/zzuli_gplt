package cn.edu.zzuli.acm.job;

import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.enums.RankConst;
import cn.edu.zzuli.acm.service.RankJsonService;
import cn.edu.zzuli.acm.to.ICPCRankTo;
import cn.edu.zzuli.acm.util.PythonExecuteUtils;
import cn.edu.zzuli.acm.vo.ICPCRankVo;
import cn.edu.zzuli.acm.ws.LinkEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ICPCRankRunner implements Runnable {

    @Autowired
    RankJsonService rankJsonService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    @Override
    public void run() {
        //这里处理 icpc赛制的 定时任务。
        //执行 Python 脚本
        PythonExecuteUtils.doRankingPython(null);
        //读取 redis中的数据，然后进行读取。
        List<ICPCRankTo> icpcRankTos = rankJsonService.getICPCRankFromRedis();
        Map<String, Object> firstSubmit = (Map<String, Object>) rankJsonService.getFirstSubmit(icpcRankTos);

        //然后将最新的数据存入到 redis中。
        redisTemplate.opsForValue().set(RankConst.ICPC_INFO.KEY(), icpcRankTos);

        //通过 websocket 将数据推送到前端。
        LinkEndpoint.broadCastToAllUsers(
                new R().add("ranking",
                        new ICPCRankVo()
                                .setIcpcRanks(icpcRankTos)
                                .setTotal(icpcRankTos.size()))
                .add("firstAc", firstSubmit)
        );

    }

}
