package cn.edu.zzuli.acm;

import cn.edu.zzuli.acm.entity.Result;
import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.enums.RankConst;
import cn.edu.zzuli.acm.mapper.ResultMapper;
import cn.edu.zzuli.acm.to.ICPCRankTo;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RankResultTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ResultMapper resultMapper;

    @Test
    void testICPCFirst() {
        List<ICPCRankTo> icpcRankTos = (List<ICPCRankTo>) redisTemplate.opsForValue().get(RankConst.ICPC_INFO.KEY());

        HashMap<String, JSONObject> firstMap = new HashMap<>();

        icpcRankTos.stream().forEach(icpcRankTo -> {
            TreeMap<String, Object> problemScores =
                    icpcRankTo.getProblemScores();

            problemScores.forEach((k , v) -> {
                JSONObject score = (JSONObject) v;
                if (score.getInteger("score") > 0) {
                    //没有这个key的时候说明是第一次遍历到 这个 key，我们只需要put就行了
                    if (!firstMap.containsKey(k)) {
                        JSONObject firstInfo = new JSONObject();
                        firstInfo.put("acceptTime", score.getInteger("acceptTime"));
                        firstInfo.put("name", icpcRankTo.getUser().getName());
                        firstInfo.put("startAt", icpcRankTo.getStartAt());
                        firstMap.put(k, firstInfo);
                    } else {
                        JSONObject lastInfo = firstMap.get(k);
                        //如果当前 acceptTime 比之前的更小一些，说明这个队伍提交的更少
                        if (lastInfo.getInteger("acceptTime")
                                > score.getInteger("acceptTime")) {
                            //更新信息
                            lastInfo.put("acceptTime", score.getInteger("acceptTime"));
                            lastInfo.put("name", icpcRankTo.getUser().getName());
                            lastInfo.put("startAt", icpcRankTo.getStartAt());
                            firstMap.put(k, lastInfo);
                        } else if (lastInfo.getInteger("acceptTime")
                                == score.getInteger("acceptTime")) {
                            //如果两次的 acceptTime 相等的话，我们必须比较他们的开始时间
                            //谁的开始时间晚，谁就是最快提交记录
                            //这里比如 "2020-11-21T04:00:12Z".compareTo("2020-11-21T04:00:08Z") 返回结果是1
                            if (icpcRankTo.getStartAt()
                                    .compareTo(lastInfo.getString("startAt")) > 0) {
                                //所以当 这个比较大于 0的时候，我们去更新map中的提交记录信息
                                lastInfo.put("acceptTime", score.getInteger("acceptTime"));
                                lastInfo.put("name", icpcRankTo.getUser().getName());
                                lastInfo.put("startAt", icpcRankTo.getStartAt());
                                firstMap.put(k, lastInfo);
                            }
                        }
                    }
                }
            });
        });

        firstMap.forEach((k ,v) -> {
            System.out.println(k + ":::::" + v);
        });
    }

    @Test
    void testRankResultTest() {

        Result result = new Result();

        //当前比赛的stands
        Stands stands = (Stands) redisTemplate.opsForValue().get("stands");
        Map<String, Object> problems_set = (Map<String, Object>) redisTemplate.opsForValue().get("problems_set");
        String problemInfo = JSONObject.toJSONString(problems_set);

        Object userRanking =  redisTemplate.opsForValue().get("userRanking");
        Object teamRanking =  redisTemplate.opsForValue().get("teamRanking");


        result.setRankUserInfo(userRanking.toString());
        result.setRankTeamInfo(teamRanking.toString());
        result.setRankTeamInfo(problemInfo);
        //当前比赛机制为天梯赛
        result.setIsIcpc(0);
        result.setRankProblemId("1595469283415740416");
        result.setStands(JSONObject.toJSONString(stands));

        resultMapper.insert(result);
    }

}
