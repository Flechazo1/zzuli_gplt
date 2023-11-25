package cn.edu.zzuli.acm.util;

import cn.edu.zzuli.acm.entity.ProblemsubmitStatu;
import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.entity.UserSumScore;
import cn.edu.zzuli.acm.service.ProblemsubmitStatuService;
import cn.edu.zzuli.acm.service.UserService;

import cn.edu.zzuli.acm.to.ProblemSubmit;
import cn.edu.zzuli.acm.vo.ProblemItemScore;
import cn.edu.zzuli.acm.vo.UserScore;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.sound.midi.Soundbank;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SumScoreUtils {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private UserService userService;

    //String[] labels = {"7-1", "7-2", "7-3", "7-4", "7-5", "7-6", "7-7", "7-8", "7-9", "7-10", "7-11", "7-12"};

    //生成每个用户对应的每题的分数：注意这里只是保存到redis，为了加快速度，这里没有计算总分
    public void createSingleScore(){
        //从redis中获取到新的提交记录
        //redisTemplate.opsForValue().set("scoreUpdateFlag", false);
        List<JSONObject> list = (List<JSONObject>) redisTemplate.opsForValue().get("cur_data");
        for(JSONObject object : list){
            //System.out.println(object.toJSONString());
            ProblemSubmit problemSubmit = object.toJavaObject(ProblemSubmit.class);
//            System.out.println();
            String key = "User" + problemSubmit.getUser().getStudentUser().getStudentNumber();
            //如果没有的话，这里会自动创建一个key，其中的内容是null
            BoundHashOperations<String, String, Object> ops = redisTemplate.boundHashOps(key);
            if(problemSubmit.getProblemType().equals("PROGRAMMING")){
                //如果确实是编程题目
                Boolean keyExist = ops.hasKey(problemSubmit.getProblemSetProblem().getLabel());
                if(!keyExist){
                    //如果首次记录成员分数，则直接放入到map
                    ops.put(problemSubmit.getProblemSetProblem().getLabel(), problemSubmit.getScore());
                    //problemSubmit.getSubmitAt() 里边记录最后一次有效提交发生的时间
                    //此时有新提交的记录，则下次算分的定时任务，则需要去重新计算分数
                    redisTemplate.opsForValue().set("scoreUpdateFlag", true);
                    ops.put("lastSubmitAt", problemSubmit.getSubmitAt());
                }else{
                    //如果之前有分数，而且这次的分数要比之前的分数高，就把这次的分数放入到map当中去
                    Double score = (Double) ops.get(problemSubmit.getProblemSetProblem().getLabel());
                    if(problemSubmit.getScore() > score){
                        ops.put(problemSubmit.getProblemSetProblem().getLabel(), problemSubmit.getScore());
                        //problemSubmit.getSubmitAt() 里边记录最后一次有效提交发生的时间
                        //此时有新提交的记录，则下次算分的定时任务，则需要去重新计算分数
                        redisTemplate.opsForValue().set("scoreUpdateFlag", true);
                        ops.put("lastSubmitAt", problemSubmit.getSubmitAt());

                    }
                }
            }
            //System.out.println(problemSubmit.getProblemSetProblem().getLabel());
        }
    }

    public void fixSingleScore(){
        //从redis中获取到新的提交记录
        //redisTemplate.opsForValue().set("scoreUpdateFlag", false);
        List<JSONObject> list = (List<JSONObject>) redisTemplate.opsForValue().get("fix_data");
        for(JSONObject object : list){
            //System.out.println(object.toJSONString());
            ProblemSubmit problemSubmit = object.toJavaObject(ProblemSubmit.class);
//            System.out.println();
            String key = "User" + problemSubmit.getUser().getStudentUser().getStudentNumber();
            //如果没有的话，这里会自动创建一个key，其中的内容是null
            BoundHashOperations<String, String, Object> ops = redisTemplate.boundHashOps(key);
            if(problemSubmit.getProblemType().equals("PROGRAMMING")){
                //如果确实是编程题目
                Boolean keyExist = ops.hasKey(problemSubmit.getProblemSetProblem().getLabel());
                if(!keyExist){
                    //如果首次记录成员分数，则直接放入到map
                    ops.put(problemSubmit.getProblemSetProblem().getLabel(), problemSubmit.getScore());
                    //problemSubmit.getSubmitAt() 里边记录最后一次有效提交发生的时间
                    //此时有新提交的记录，则下次算分的定时任务，则需要去重新计算分数
                    redisTemplate.opsForValue().set("scoreUpdateFlag", true);
                    ops.put("lastSubmitAt", problemSubmit.getSubmitAt());
                }else{
                    //如果之前有分数，而且这次的分数要比之前的分数高，就把这次的分数放入到map当中去
                    Double score = (Double) ops.get(problemSubmit.getProblemSetProblem().getLabel());
                    if(problemSubmit.getScore() > score){
                        ops.put(problemSubmit.getProblemSetProblem().getLabel(), problemSubmit.getScore());
                        //problemSubmit.getSubmitAt() 里边记录最后一次有效提交发生的时间
                        //此时有新提交的记录，则下次算分的定时任务，则需要去重新计算分数
                        redisTemplate.opsForValue().set("scoreUpdateFlag", true);
                        ops.put("lastSubmitAt", problemSubmit.getSubmitAt());

                    }
                }
            }
            //System.out.println(problemSubmit.getProblemSetProblem().getLabel());
        }
    }

    //得到用户的实际得分label.matches("1-\\w*")
    public List<UserScore> getUserScore(){
        List<UserScore> userScores = new LinkedList<>();
        //
        Set<String> users = redisTemplate.keys("User*");

        for(String user : users){
            String userNumber = user.substring(4);
            UserScore userScore = new UserScore();
            List<ProblemItemScore> scoreList1 = new LinkedList<>();
            List<ProblemItemScore> scoreList2 = new LinkedList<>();
            List<ProblemItemScore> scoreList3 = new LinkedList<>();
            BoundHashOperations<String, String, Object> ops = redisTemplate.boundHashOps(user);
            Set<String> keys = ops.keys();
            for(String label : keys){
                ProblemItemScore itemScore = new ProblemItemScore(1, label, ((Double) ops.get(label)));
                if(label.matches("1-\\w*")){
                    scoreList1.add(itemScore);
                    //基础题目得分
                    if(userScore.getPrimaryScore()==null){
                        userScore.setPrimaryScore(0.0);
                    }
                    userScore.setPrimaryScore(userScore.getPrimaryScore()+((Double) ops.get(label)));
                }else if(label.matches("2-\\w*")){
                    scoreList2.add(itemScore);
                    //进阶题目得分
                    if(userScore.getAdvanceScore()==null){
                        userScore.setAdvanceScore(0.0);
                    }
                    userScore.setAdvanceScore(userScore.getAdvanceScore()+((Double) ops.get(label)));
                }else if(label.matches("3-\\w*")){
                    scoreList3.add(itemScore);
                    //高级题目得分
                    if(userScore.getSeniorScore()==null){
                        userScore.setSeniorScore(0.0);
                    }
                    userScore.setSeniorScore(userScore.getSeniorScore()+((Double) ops.get(label)));
                }else{
                    log.info("编号有误");
                }
            }

            userScore.setPrimaryScoreItems(scoreList1);
            userScore.setAdvanceScoreItems(scoreList2);
            userScore.setSeniorScoreItems(scoreList3);
            User uuser = new User();
            uuser.setStuId(userNumber);
            userScore.setUser(uuser);
            userScores.add(userScore);
        }
        return userScores;
    }

    //得到用户的实际得分
    public List<UserScore> getVirUserScore(){
        LocalDateTime start = LocalDateTime.now();
        CopyOnWriteArrayList<UserScore> userScores = new CopyOnWriteArrayList<>();
        Set<String> users = redisTemplate.keys("User*");
        Stands stands = (Stands) redisTemplate.opsForValue().get("stands");
        List<User> userList = userService.list();

        users.stream().parallel().forEach( user -> {
            //long startTime1 = System.currentTimeMillis();
            String userNumber = user.substring(4);
            UserScore userScore = new UserScore();
            userScore.setPrimaryScore(0.0);
            userScore.setAdvanceScore(0.0);
            userScore.setSeniorScore(0.0);
            List<ProblemItemScore> scoreList1 = new LinkedList<>();
            List<ProblemItemScore> scoreList2 = new LinkedList<>();
            List<ProblemItemScore> scoreList3 = new LinkedList<>();

            BoundHashOperations<String, String, Object> ops = redisTemplate.boundHashOps(user);
            Map<String, Object> map = ops.entries();  //这个地方是问题的关键，耗费最长的时间计算，现在的话300人在线做题的话，计算一次20s左右
            Set<String> labels = map.keySet();
            //初始化以后map中未提交的题目都是0.0分，所以说，我们可以不获取，直接遍历数组
//            long startTime1 = System.currentTimeMillis();

            for(String label : labels){
                //long startTime3 = System.currentTimeMillis();
                if (label.equals("lastSubmitAt")) {
                    String lastSubmitAt = map.get("lastSubmitAt").toString();
                    userScore.setLastSubmitAt(lastSubmitAt);
                    continue;
                }
                ProblemItemScore itemScore = new ProblemItemScore(Integer.parseInt(label.split("-")[1]),label, ((Double) map.get(label)));
                //System.out.println(label+"得分："+itemScore.getScore());
//                if(label.equals("7-1")||label.equals("7-2")||label.equals("7-3")||label.equals("7-4")){
//                    scoreList1.add(itemScore);
//                    //基础题目得分
//                    userScore.setPrimaryScore(userScore.getPrimaryScore()+itemScore.getScore());
//                }else if(label.equals("7-5")||label.equals("7-6")||label.equals("7-7")||label.equals("7-8")){
//                    scoreList2.add(itemScore);
//                    //进阶题目得分
//                    userScore.setAdvanceScore(userScore.getAdvanceScore()+itemScore.getScore());
//                }else if(label.equals("7-9")||label.equals("7-10")||label.equals("7-11")||label.equals("7-12")){
//
//                    scoreList3.add(itemScore);
//                    //高级题目得分
//                    userScore.setSeniorScore(userScore.getSeniorScore()+itemScore.getScore());
//
//                }else{
//                    log.info("编号有误");
//                }
                if(label.matches("1-\\w*")){
                    scoreList1.add(itemScore);
                    //基础题目得分
                    userScore.setPrimaryScore(userScore.getPrimaryScore()+itemScore.getScore());
                }else if(label.matches("2-\\w*")){
                    scoreList2.add(itemScore);
                    //进阶题目得分
                    userScore.setAdvanceScore(userScore.getAdvanceScore()+itemScore.getScore());
                }else if(label.matches("3-\\w*")){
                    scoreList3.add(itemScore);
                    //高级题目得分
                    userScore.setSeniorScore(userScore.getSeniorScore()+itemScore.getScore());

                }else{
                    log.info("编号有误");
                }

            }

//            long startTime2 = System.currentTimeMillis();
//            System.out.println((startTime2-startTime1)+"ms");
            userScore.setSumScore(userScore.getPrimaryScore());
            if(userScore.getPrimaryScore()>=stands.getPrimaryscore()){
                //如果当前基础分数过线，则加上进阶分数
                userScore.setSumScore(userScore.getSumScore()+userScore.getAdvanceScore());
                if(userScore.getAdvanceScore()>=stands.getAdvancescore()){
                    //如果当前进阶分过线，则加上高级分数
                    userScore.setSumScore(userScore.getSumScore()+userScore.getSeniorScore());
                }
            }
            Collections.sort(scoreList1, Comparator.comparing(ProblemItemScore::getId));
            Collections.sort(scoreList2, Comparator.comparing(ProblemItemScore::getId));
            Collections.sort(scoreList3, Comparator.comparing(ProblemItemScore::getId));
            userScore.setPrimaryScoreItems(scoreList1);
            userScore.setAdvanceScoreItems(scoreList2);
            userScore.setSeniorScoreItems(scoreList3);
//            QueryWrapper<User> wrapper = new QueryWrapper<>();
//            wrapper.eq("stu_id", userNumber);
//            User uuser = userService.getOne(wrapper);
//            userScore.setUser(uuser);
            for(User user1 : userList){
                if(user1.getStuId().equals(userNumber)){
                    userScore.setUser(user1);
                    userScores.add(userScore);
                    break;
                }
            }
            //long startTime2 = System.currentTimeMillis();
//            System.out.println("用时："+(startTime2-startTime1)+"S");
        });
        LocalDateTime end = LocalDateTime.now();
        Duration duration = Duration.between(start, end);
        System.out.println(duration.toMillis());
        return userScores;
    }
}
