package cn.edu.zzuli.acm.service.impl;

import cn.edu.zzuli.acm.entity.ProblemsubmitStatu;
import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.enums.InfoConst;
import cn.edu.zzuli.acm.enums.RankConst;
import cn.edu.zzuli.acm.mapper.ProblemsubmitStatuMapper;
import cn.edu.zzuli.acm.service.ProblemsubmitStatuService;
import cn.edu.zzuli.acm.service.RankJsonService;
import cn.edu.zzuli.acm.service.UserService;
import cn.edu.zzuli.acm.to.*;
import cn.edu.zzuli.acm.util.PythonExecuteUtils;
import cn.edu.zzuli.acm.util.RedisUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RankJsonServiceImpl implements RankJsonService {

    @Autowired
    ProblemsubmitStatuService problemsubmitStatuService;

    @Autowired
    UserService userService;

    @Autowired
    ProblemsubmitStatuMapper problemsubmitStatuMapper;

//    @Autowired
//    SumScoreUtils sumScoreUtils;

    @Autowired
    PythonExecuteUtils pythonExecuteUtils;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    RedisUtil redisUtil;

    //parseJsonInfoToDB
    public boolean parseJsonInfoToDB(List<JSONObject> list) {

        CopyOnWriteArrayList<ProblemsubmitStatu> problemsubmitStatus
                = new CopyOnWriteArrayList<>();

        list.parallelStream().filter(jsonObject -> {
            return jsonObject != null;
         }).collect(Collectors.toList()).forEach(jsonObject -> {
            //先解析成ProblemSubmitStatu对象再做打算
            ProblemSubmit object = jsonObject.toJavaObject(ProblemSubmit.class);
            //获取到题目信息
            ProblemSet problemSet = object.getProblemSetProblem();
            //获取到提交信息,将该对象存入到 db
            ProblemsubmitStatu problemsubmitstatu = new ProblemsubmitStatu();
            //设置当前的题目集id
            problemsubmitstatu.setProblemId(PythonExecuteUtils.getProblemId());
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

//            ProblemsubmitStatu problemFromDb = problemsubmitStatuMapper.selectOne(
//                    new LambdaQueryWrapper<ProblemsubmitStatu>()
//                            .eq(ProblemsubmitStatu::getSn, problemsubmitstatu.getSn())
//            );
//            if (problemFromDb == null) {
//                System.out.println("找到啦");
                //获取到成员信息
                UserV userV = object.getUser();
                //获取学号
                String number = userV.getStudentUser().getStudentNumber();
                if (number.equals("542007040117")) {
                    number = "542007040210";
                }
                //确定个人
                LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.eq(User::getStuId, number);

                User user = userService.getOne(userWrapper);
                if (user != null) problemsubmitStatus.add(problemsubmitstatu);
                problemsubmitstatu.setUserid(user.getId());
//            }


            //TODO 正式上线会 EXCEL 导入User表。所以无需该Synchronized代码块
//            synchronized (this) {
//                User user = userService.getOne(userWrapper);
//                if(user == null){
//                    //录入这个学生
//                    user = new User();
//                    user.setNickname(userV.getUser().getNickname());
//                    user.setUsername(userV.getStudentUser().getName());
//                    user.setSn(userV.getUser().getId())
//                            .setStuId(userV.getStudentUser().getStudentNumber());
//                    user.setAddTime(LocalDateTime.now());
//                    userService.save(user);
//
//                }
//                problemsubmitstatu.setUserid(user.getId());
//            }
        });

        try {
            problemsubmitStatuService.saveBatch(problemsubmitStatus);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * 第一次爬取信息的时候使用这个方法。
     * 直到我们的  after_id != null;
     * 这样我们下次调用的时候就可以查询 after_id 之后的来获取最新数据。
     *
     *
     * 这个方法是不断的向前爬取，直到第一条数据。
     * 因为 平台限制，我们最多爬取100条数据，那么如果当前第一次情况就出现了120条数据，
     * 这样的话，前20条是获取不断地，所以我们必须再爬取一次
     *
     * @return after_id
     */
    public String initRankJsonFromTheFile() {
        String after_id = null;

        Boolean hasBefore = true;
        String before_id = null;

        while (hasBefore) {
            List<JSONObject> submissions = new ArrayList<>(100);

            //发送请求，执行python 脚本
            if (before_id == null) {
                pythonExecuteUtils.ExecutePython();
            }else {
                pythonExecuteUtils.selectBeforeTheId(before_id, "not limit");
            }

            //将数据解析成json
            JSONObject rankJson = getRankJsonInfoFromTheFile();
            submissions.addAll((List<JSONObject>) rankJson.get("submissions"));
            if (submissions.size() == 0) {
                return "no id";
            }

            hasBefore = rankJson.getBoolean("hasBefore");

            //修改变量的值, 方便以后我们查询。
            if (after_id == null) after_id = submissions.get(0).get("id").toString();
            before_id = submissions.get(submissions.size() - 1).get("id").toString();

            parseJsonInfoToDB(submissions);

            try {
                TimeUnit.SECONDS.sleep(4);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        //存到db
        System.out.println("to db --->>>>>>>>>> success");
        return after_id;

    }


    /**
     * 获取这个 id之后的数据，经由计算，最多 100 条，但我们这里可以算出50条的。
     * @param id
     * @param limit
     * @return
     */
    @Override
    public String getRankJsonFromAfterTheId(String id, String limit) {
        if (id == null) return null;
        if (StringUtils.isEmpty(limit)) limit = "not limit";
        PythonExecuteUtils.selectAfterTheId(id, limit);

        //这个时候的 rank.json 文件，就是我们当前最新的 limit 条记录啦
        //从文件中获取最新的 数据
        List<JSONObject> submissions =
                (List<JSONObject>) getRankJsonInfoFromTheFile().get("submissions");

        //将这个数据保存到 redis 中，并且取出第一个作为 after_id;
        if (submissions.size() == 0) {
            //判断是不是最新的。
            String after_id = (String) redisUtil.get("after_id");
            //如果是no_id 说明定时启动的时候一直没爬到最新数据,所以我们一直要默认查询的
            //如果不是no_id，说明我们的项目已经爬到这个 id了，所以下次调用的时候我们只需要从这个id开始就行
            if ("no id".equals(after_id)) {
                return "no id";
            }
            return after_id;
        }


        //存到db
        parseJsonInfoToDB(submissions);

        return submissions.get(0).get("id").toString();
    }

    @Override
    public void fixScore() {
    }

    /**
     * 从rank.json 文件中，获取最新的 json数据
     * 返回的类型是 json字符串
     * @return json字符串
     */
    public JSONObject getRankJsonInfoFromTheFile() {
        BufferedReader br = null;
        try {
            //从文件中读取数据
            FileReader fileReader = new FileReader("rank.json");
            br = new BufferedReader(fileReader);
            String line = null;
            StringBuilder str = new StringBuilder();
            while((line = br.readLine())!=null) {
                str.append(line);
            }

            //将数据解析成json
            JSONObject rankJson = JSON.parseObject(str.toString());
            return rankJson;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     *
     * @return
     */
    @Override
    public void getRankJsonInfo() {
        //判断是不是第一次查询
        String after_id = (String) redisUtil.get("after_id");

        log.info("after_id: " + after_id);

        if (after_id != null) {
            log.info("有 after_id , 执行 after...");
            //然后接下来就是一直遍历之后了...
            after_id = getRankJsonFromAfterTheId(after_id, "100");
        }else {
            //第一次查询，直到 after_id != null
            log.info("没有 after_id, init ....");
            do {
                after_id = initRankJsonFromTheFile();
            } while (after_id == null);
        }

        //在redis生成个人分数的结构
//        sumScoreUtils.createSingleScore();

        //将这个 after_id 标志位存入到 redis 中
        redisUtil.set("after_id", after_id);
    }


    public List<RankTo> getRankingFromRedis() {
        JSONArray jsonArray = (JSONArray) redisUtil.get("ranking_json");
        if (jsonArray == null) {
            log.warn("python not execute");
        }

        List<RankTo> rankTos = jsonArray.toJavaList(RankTo.class);
        Stands stands = (Stands) redisUtil.get("stands");
//        userService.list()
//                .stream()
//                .collect(Collectors.toMap(
//                        User::getId, user -> user
//                ));

        rankTos.parallelStream().forEach(rankTo -> {

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
            String number = jsonUser.getStudentNumber();
            //TODO 上线时记得更改。
            if (number.equals("542007040117")) number = "542007040210";
            User user = userService.getOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getStuId, number));

//            User user = userMap.get(jsonUser.getStudentNumber());
            if (user != null) {
                jsonUser.setStudentNumber(user.getStuId());
                jsonUser.setName(user.getUsername());
                jsonUser.setTeamId(user.getTeamid());
                jsonUser.setTheClass(user.getTheClass());
            }
        });

        //记录队伍的分。顺道重新计算 rank排名数
        List<RankTo> rankTosSorted = rankTos.stream().sorted((rankTo1, rankTo2) -> {
            return rankTo2.getTotalScore() - rankTo1.getTotalScore();
        }).collect(Collectors.toList());

        int lastTotal = 0;
        int equalNumber= 0;
        int rank = 1;

        //problems_set map.用来初始化一些所有题目的默认值，比如1-1:0, 1-2:0, 1-3:0
        //这里要注意的是，我并没有按照 1-1，1-2，1-3这样排序，再treeMap中，1-11 会排在 1-2的前边
        TreeMap<String, Integer> problems_set
                = (TreeMap<String, Integer>) redisUtil.get("problems_set");

        for (RankTo rankTo : rankTosSorted) {
            TreeMap<String, Object> problemScores = rankTo.getProblemScores();
            if(problems_set != null){
                problems_set.forEach((k ,v) -> {
                    if (problemScores.get(k) == null) {
//                    System.out.println(k);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("score", 0.0);
                        jsonObject.put("acceptTime", 0);
                        problemScores.put(k, jsonObject);
                    }
                });
            }

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

        return rankTosSorted;
    }

    @Override
    public List<ICPCRankTo> getICPCRankFromRedis() {
        JSONArray jsonArray = (JSONArray) redisUtil.get("ranking_json");
        if (jsonArray == null) log.warn("python not execute");
        List<ICPCRankTo> icpcRankTos = jsonArray.toJavaList(ICPCRankTo.class);

        icpcRankTos.forEach(icpcRankTo -> {
            //如果前端需要我们补充一些字段的话，那这里的逻辑就变为了
            //过滤调 score为0的题目
            //这里我们相信前端的能力，实在不行我们再改
            TreeMap<String, Object> problemScores = icpcRankTo.getProblemScores();
            AtomicInteger n = new AtomicInteger();
            problemScores.forEach((k,v) -> {
                JSONObject problems = (JSONObject) v;
                if (((JSONObject) v).getInteger("score") != 0){
                    n.getAndIncrement();
                }
            });
            icpcRankTo.setAcCount(n.get());
        });

        //TODO 去数据库里比较，剔除不在数据库的队伍
//        icpcRankTos.forEach(System.out::println);
        return icpcRankTos;
    }

    public Map<String,? extends Object> getFirstSubmit(List<ICPCRankTo> icpcRankTos) {
        //判断需不需要，计算出各个题目的是被那个队伍第一个ac的
        Map<String, Object> problem_set = (Map<String, Object>) redisUtil.get(InfoConst.PROBLEM_SET.KEY());
        System.out.println(problem_set);
        Map<String, Object> firstSubmit = (Map<String, Object>) redisUtil.get(RankConst.FIRST_SUBMIT.KEY());
        if (firstSubmit != null && problem_set.size() == firstSubmit.size()) {
            return firstSubmit;
        }

        HashMap<String, JSONObject> firstMap = new HashMap<>();

        icpcRankTos.stream().forEach(icpcRankTo -> {
            TreeMap<String, Object> problemScores =
                    icpcRankTo.getProblemScores();

            problemScores.forEach((k , v) -> {
                JSONObject score = (JSONObject) v;
                //过滤掉 错误的提交。
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

        //记录下最新的 first ac提交记录
        redisUtil.set(RankConst.FIRST_SUBMIT.KEY(), firstMap);
//        firstMap.forEach((k ,v) -> {
//            System.out.println(k + ":::::" + v);
//        });

        return firstMap;
    }

}
