package cn.edu.zzuli.acm;

import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.service.UserService;
import cn.edu.zzuli.acm.to.ICPCRankTo;
import cn.edu.zzuli.acm.to.RankTo;
import cn.edu.zzuli.acm.to.StudentUser;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ScoreTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;


    @Test
    void testGetRankingFromRedis() {
        LocalDateTime start = LocalDateTime.now();
        JSONArray jsonArray = (JSONArray) redisTemplate.opsForValue().get("ranking_json");
//        System.out.println(jsonArray);
        List<RankTo> rankTos = jsonArray.toJavaList(RankTo.class);
        Stands stands = (Stands) redisTemplate.opsForValue().get("stands");
//
//        Map<String, User> userMap = userService.list()
//                .stream()
//                .collect(Collectors.toMap(
//                        User::getStuId, user -> user
//                ));

        rankTos.parallelStream().forEach(rankTo -> {
//            System.out.println(rankTo);
            Map<String, Object> problemScores =
                    rankTo.getProblemScores();
            Integer totalScore = 0;

            //计算该用户三个阶段的分数
            problemScores.forEach( (label, scoreJson) -> {
                int score = ((JSONObject)scoreJson).getInteger("score");
                if(label.matches("1-\\w*")){
                    //基础阶段的分数
                    rankTo.setPrimaryScore(rankTo.getPrimaryScore() + score);
                }else if (label.matches("2-\\w*")){
                    //进阶阶段的分数
                    rankTo.setAdvanceScore(rankTo.getAdvanceScore() + score);
                }else if (label.matches("3-\\w*")){
                    //高级阶段的分数
                    rankTo.setSeniorScore(rankTo.getSeniorScore() + score);
                }else {
                    System.out.println("label 有误, 不计分");
                }
            });

            totalScore = rankTo.getPrimaryScore();
            //然后根据标准分，然后重新计算 total分数
            if(rankTo.getPrimaryScore() >= stands.getPrimaryscore()){
                //如果当前基础分数过线，则加上进阶分数
                totalScore += rankTo.getAdvanceScore();
                if(rankTo.getAdvanceScore() >= stands.getAdvancescore()){
                    //如果当前进阶分过线，则加上高级分数
                    totalScore += rankTo.getSeniorScore();
                }
            }
            rankTo.setTotalScore(totalScore);
            StudentUser jsonUser = rankTo.getUser();
            User user = userService.getOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getStuId, jsonUser.getStudentNumber()));

//            User user = userMap.get(jsonUser.getStudentNumber());
            if (user != null) {
                jsonUser.setTeamId(user.getTeamid());
                jsonUser.setTheClass(user.getTheClass());
            }
        });

        //记录队伍的分。顺道重新计算 rank排名数
        List<RankTo> rankTosSorted = rankTos.stream().sorted((rankTo1, rankTo2) -> {
            return rankTo2.getTotalScore() - rankTo1.getTotalScore();
        }).collect(Collectors.toList());

        rankTosSorted.forEach(System.out::println);
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        System.out.println(duration.toMillis());
    }

    @Autowired
    UserService userService;

    @Test
    void testGetUserMap() {
        Map<String, User> collect = userService.list()
                .stream()
                .collect(Collectors.toMap(
                        User::getStuId, user -> user
                ));
        collect.forEach((k, v) -> {
            System.out.println(k +":::" + v);
        });
    }

    @Test
    void testSortedRanking() {
        List<RankTo> userRanking = (List<RankTo>) redisTemplate.opsForValue().get("userRanking");

        int lastTotal = 0;
        int equalNumber= 0;
        int rank = 1;

        for (RankTo rankTo : userRanking) {

            if (rankTo.getTotalScore() != lastTotal) {
                rank += equalNumber;
                rankTo.setRank(rank);
                rank += 1;
                equalNumber = 0;
            }else {
                equalNumber++;
                rankTo.setRank(rank - 1);
            }
            lastTotal = rankTo.getTotalScore();
        }
        userRanking.forEach(rankTo -> {
            System.out.println("rank: " + rankTo.getRank()
                    + ", score: "+ rankTo.getTotalScore()
                    + ", user: " + rankTo.getUser().getName()
                    +",teamid: " + rankTo.getUser().getTeamId()   );
        });

    }

    @Test
    void testICPCRank() {
        JSONArray jsonArray = (JSONArray) redisTemplate.opsForValue().get("ranking_json");
        List<ICPCRankTo> icpcRankTos = jsonArray.toJavaList(ICPCRankTo.class);

        //icpc 的rank很简单，按照答题数来排名，答题数相同的话，那么按照花费的时间来排名
        //一道题的时间是从比赛开始，一直到这道题被ac
        //比如1-3，在比赛开始后15分钟时ac，那么这道题就花费了15分钟
        //如果在ac前，答错了2次，那么会具有罚时机制，一次wa 会罚时20分钟
        //不过这里我们无需担心，因为我们这些数据都已经被python脚本爬取并且放在redis中了。


        icpcRankTos.forEach( icpcRankTo -> {
            System.out.println(icpcRankTo.getProblemScores());
        });
    }

}
