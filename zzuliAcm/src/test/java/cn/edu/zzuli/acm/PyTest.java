package cn.edu.zzuli.acm;

import cn.edu.zzuli.acm.entity.ProblemsubmitStatu;
import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.service.ProblemsubmitStatuService;
import cn.edu.zzuli.acm.service.RankJsonService;
import cn.edu.zzuli.acm.service.TeamService;
import cn.edu.zzuli.acm.service.UserService;
import cn.edu.zzuli.acm.to.ProblemSet;
import cn.edu.zzuli.acm.to.ProblemSubmit;
import cn.edu.zzuli.acm.to.UserV;
import cn.edu.zzuli.acm.util.PythonExecuteUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@SpringBootTest
public class PyTest {

    @Autowired
    PythonExecuteUtils pythonExecuteUtils;

    @Autowired
    RankJsonService rankJsonService;

    @Autowired
    ProblemsubmitStatuService problemsubmitstatuService;

    @Autowired
    UserService userService;

    @Autowired
    TeamService teamService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    int n = 1;

    @Test
    void testOptimizeObjectToDB(List<JSONObject> list) {
        long startTime1 = System.currentTimeMillis();
//        List<JSONObject> list = rankJsonService.getRankJsonInfo();

        CopyOnWriteArrayList<ProblemsubmitStatu> problemsubmitStatus
                = new CopyOnWriteArrayList<>();

        list.parallelStream().forEach( jsonObject -> {
            //先解析成ProblemSubmitStatu对象再做打算
            ProblemSubmit object = jsonObject.toJavaObject(ProblemSubmit.class);
//            System.out.println(object.toString());
            //获取到题目信息
            ProblemSet problemSet = object.getProblemSetProblem();
            //获取到提交信息,将该对象存入到 db
            ProblemsubmitStatu problemsubmitstatu = new ProblemsubmitStatu();
            BeanUtils.copyProperties(object, problemsubmitstatu);
            problemsubmitstatu.setProblemtype(object.getProblemType());
            if(problemsubmitstatu.getProblemtype().equals("PROGRAMMING")){
                //如果是编程题目，是有题号的
                problemsubmitstatu.setProbleminfolabel(problemSet.getLabel());
            }else{
                problemsubmitstatu.setProbleminfolabel(problemsubmitstatu.getProblemtype());
            }
            problemsubmitstatu.setSubmitat(LocalDateTime.parse(object.getSubmitAt().split("Z")[0]));
            if(object.getShowDetail()){
                problemsubmitstatu.setShowdetail(1);
            }else{
                problemsubmitstatu.setShowdetail(0);
            }
            if(object.getPreviewSubmission()){
                problemsubmitstatu.setPreviewsubmission(1);
            }else{
                problemsubmitstatu.setPreviewsubmission(0);
            }
            problemsubmitstatu.setSn(object.getId());
            //获取到成员信息
            UserV userV = object.getUser();
            //获取学号
            String number = userV.getStudentUser().getStudentNumber();
            //确定个人
            QueryWrapper<User> userWrapper = new QueryWrapper<>();
            userWrapper.eq("stu_id", number);
            synchronized (this) {
                User user = userService.getOne(userWrapper);
                if(user == null){
                    //录入这个学生
                    user = new User();
                    user.setNickname(userV.getUser().getNickname());
                    user.setUsername(userV.getStudentUser().getName());
                    user.setSn(userV.getUser().getId())
                            .setStuId(userV.getStudentUser().getStudentNumber());
                    user.setAddTime(LocalDateTime.now());
                    userService.save(user);

                }
                problemsubmitstatu.setUserid(user.getId());
            }

            problemsubmitStatus.add(problemsubmitstatu);

        });

        problemsubmitstatuService.saveBatch(problemsubmitStatus);


        long endTime1 = System.currentTimeMillis();    //获取结束时间

        System.out.println("代码运行时间：" + ((endTime1 - startTime1)) + "s");
    }

    @Test
    void testSortByDate() {
        BoundHashOperations<String, Object, Object> user542007040207 = redisTemplate.boundHashOps("User542007040207");
        String user542007040207_lastSubmitAt = (String) user542007040207.get("lastSubmitAt");
        System.out.println(user542007040207_lastSubmitAt);

        BoundHashOperations<String, Object, Object> User542007040209 = redisTemplate.boundHashOps("User542007040209");
        String User542007040209_lastSubmitAt = (String) User542007040209.get("lastSubmitAt");
        System.out.println(User542007040209_lastSubmitAt);

        System.out.println(user542007040207_lastSubmitAt.compareTo(User542007040209_lastSubmitAt));
    }

//    @Test
//    void testObject(){
//        long startTime1 = System.currentTimeMillis();
////        List<JSONObject> list = rankJsonService.getRankJsonInfo();
////        List<ProblemsubmitStatu> problemsubmitStatus = new ArrayList<>(list.size());
//        for (JSONObject jsonObject : list) {
//            //先解析成ProblemSubmitStatu对象再做打算
//            ProblemSubmit object = jsonObject.toJavaObject(ProblemSubmit.class);
//            System.out.println(object.toString());
//            //获取到题目信息
//            ProblemSet problemSet = object.getProblemSetProblem();
//            //获取到提交信息,将该对象存入到 db
//            ProblemsubmitStatu problemsubmitstatu = new ProblemsubmitStatu();
//            BeanUtils.copyProperties(object, problemsubmitstatu);
//            problemsubmitstatu.setProblemtype(object.getProblemType());
//            if(problemsubmitstatu.getProblemtype().equals("PROGRAMMING")){
//                //如果是编程题目，是有题号的
//                problemsubmitstatu.setProbleminfolabel(problemSet.getLabel());
//            }else{
//                problemsubmitstatu.setProbleminfolabel(problemsubmitstatu.getProblemtype());
//            }
//            problemsubmitstatu.setSubmitat(LocalDateTime.parse(object.getSubmitAt().split("Z")[0]));
//            if(object.getShowDetail()){
//                problemsubmitstatu.setShowdetail(1);
//            }else{
//                problemsubmitstatu.setShowdetail(0);
//            }
//            if(object.getPreviewSubmission()){
//                problemsubmitstatu.setPreviewsubmission(1);
//            }else{
//                problemsubmitstatu.setPreviewsubmission(0);
//            }
//            problemsubmitstatu.setSn(object.getId());
//            //获取到成员信息
//            UserV userV = object.getUser();
//            //获取学号
//            String number = userV.getStudentUser().getStudentNumber();
//            //确定个人
//            QueryWrapper<User> userWrapper = new QueryWrapper<>();
//            userWrapper.eq("stu_id", number);
//            User user = userService.getOne(userWrapper);
//            if(user == null){
//                System.out.println("当前学生未登记");
//                //录入这个学生
//                user = new User();
//                user.setNickname(userV.getUser().getNickname());
//                user.setUsername(userV.getStudentUser().getName());
//                user.setSn(userV.getUser().getId())
//                        .setStuId(userV.getStudentUser().getStudentNumber());
//                user.setAddTime(LocalDateTime.now());
//                userService.save(user);
//            }
//            problemsubmitstatu.setUserid(user.getId());
//            problemsubmitstatuService.save(problemsubmitstatu);
//
////            problemsubmitStatus.add(problemsubmitstatu);
//            //
//            //确定队伍
////            Team team = teamService.getById(user.getTeamid());
////            if(team == null){
////                System.out.println("当前学生未组队");
////                continue;
////            }
//
//        }


//        long endTime1 = System.currentTimeMillis();    //获取结束时间
//
//        System.out.println("代码运行时间：" + ((endTime1 - startTime1)/1000) + "s");
//    }

//    @Autowired
//    SumScoreUtils sumScoreUtils;

    @Test
    void testPy() {
//        System.out.println(rankJsonService.initRankJsonFromTheFile());
//        rankJsonService.getRankJsonFromAfterTheId("1331879670982676480", null);

        rankJsonService.getRankJsonInfo();
//        PythonExecuteUtils.doRankingPython(null);
//        sumScoreUtils.getVirUserScore();
//        rankJsonService.initRankJsonFromTheFile();
//        PythonExecuteUtils.getInfoByPy("1336237048019054592");
    }

    @Test
    void testAddAll() {
        List<String> strings1 = new ArrayList<>();
        strings1.add("a");
        strings1.add("b");
        strings1.add("c");
        System.out.println(strings1.get(strings1.size() - 1));

        List<String> strings2 = new ArrayList<>();
        strings1.add("e");
        strings1.add("f");
        strings1.add("g");

        strings1.addAll(strings2);
        System.out.println(strings1);

    }

    @Test
    void readRankJson_init() throws IOException {
        LocalDateTime start = LocalDateTime.now();
        Boolean hasBefore = true;
//        List<JSONObject> submissions = new ArrayList<>(100);
        String before_id = null;

        while (hasBefore) {

            List<JSONObject> submissions = new ArrayList<>(100);
            System.out.println("第" + n + " :次, 执行一次... py");
//            if (n == 2) break;
            n++;

            //发送请求，执行python 脚本
            if (before_id == null) {
                pythonExecuteUtils.ExecutePython();
            }else {
                pythonExecuteUtils.selectBeforeTheId(before_id, "not limit");
            }

            //从文件中读取数据
            FileReader fileReader = new FileReader("rank.json");

            BufferedReader br = new BufferedReader(fileReader);

            String line = null;
            StringBuilder str = new StringBuilder();
            while((line = br.readLine())!=null) {
                str.append(line);
            }

            //将数据解析成json
            JSONObject rankJson = JSON.parseObject(str.toString());
            submissions.addAll((List<JSONObject>) rankJson.get("submissions"));

            hasBefore = rankJson.getBoolean("hasBefore");
            System.out.println(hasBefore);
            br.close();


            before_id = submissions.get(submissions.size() - 1).get("id").toString();

            //存到db
//            testOptimizeObjectToDB(submissions);
            rankJsonService.parseJsonInfoToDB(submissions);
            System.out.println("存入 db ->>>>. success");

        }



        LocalDateTime end = LocalDateTime.now();

        final Duration duration = Duration.between(start, end);
        System.out.println(duration.toMillis());
    }

}
