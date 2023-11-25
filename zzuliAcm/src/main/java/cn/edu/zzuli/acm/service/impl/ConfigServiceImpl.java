package cn.edu.zzuli.acm.service.impl;

import cn.edu.zzuli.acm.entity.Config;
import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.enums.InfoConst;
import cn.edu.zzuli.acm.enums.JobConst;
import cn.edu.zzuli.acm.mapper.ConfigMapper;
import cn.edu.zzuli.acm.service.ConfigService;
import cn.edu.zzuli.acm.service.UserService;
import cn.edu.zzuli.acm.util.PythonExecuteUtils;
import cn.edu.zzuli.acm.vo.ConfigVo;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author geji
 * @since 2020-11-30
 */
@Service
@Slf4j
public class ConfigServiceImpl extends ServiceImpl<ConfigMapper, Config> implements ConfigService {


    @Autowired
    UserService userService;

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean updateAdminConfig(Config newObj) {
        try {
            Config old = null;
            if((old = this.getById(newObj.getId())) == null){
                //此时没有用户，直接添加
                newObj.setAddTime(LocalDateTime.now());
                this.save(newObj);
                old = newObj;
            }else {
                //将最新的 sessionId  和 newObj 修改到数据库
                if (!StringUtils.isEmpty(newObj.getSession()))
                    old.setSession(newObj.getSession());
                if (!StringUtils.isEmpty(newObj.getProblemid()))
                    old.setProblemid(newObj.getProblemid());
                this.updateById(old);
            }

            //293f7994-3054-4468-ae8c-6343f712caf2
            //1336237048019054592
            //此时更新原来 SHELL 的session和problemid
            PythonExecuteUtils.updateTheSessionId(old.getSession());
            PythonExecuteUtils.updateTheProblemsId(old.getProblemid());

            //执行py 脚本执行初始化 redis信息。
            log.info("当前info 情况 --->>> session : " + old.getSession() + ", problem_id: " + old.getProblemid());
            return true;
        } catch (Exception e) {
            log.info("这里出错啦");
            return false;
        }
    }

    @Override
    public ConfigVo getAdminInfo() {
        Config config = this.getById(1);
        boolean IS_START = (boolean) redisTemplate.opsForValue().get(InfoConst.IS_START.KEY());

        //获取当前比赛的赛制是天梯赛还是ICPC赛制
        boolean IS_ICPC = (boolean) redisTemplate.opsForValue().get(InfoConst.IS_ICPC.KEY());

        //获取三个定时任务的状态
        boolean SCORE_JOB = (boolean) redisTemplate.opsForValue().get(JobConst.SCORE_JOB.KEY());
        boolean PROBLEM_JOB = (boolean) redisTemplate.opsForValue().get(JobConst.PROBLEM_JOB.KEY());
        boolean ICPCRANK_JOB = (boolean) redisTemplate.opsForValue().get(JobConst.ICPCRANK_JOB.KEY());

        return new ConfigVo(config, IS_START, IS_ICPC, SCORE_JOB, PROBLEM_JOB, ICPCRANK_JOB);
    }

    public boolean initRedisInfo(String problemId) {
        if (problemId == null) return false;
        //这里调用 Py 脚本去初始化 redis数据库信息
        PythonExecuteUtils.getInfoByPy(problemId);
        //从 problem_set_label.txt 文件中读取题目 Id 和 分数
        Map<String, Integer> map = null;
        List<User> users = null;

        //获取
//        CompletableFuture<List<User>> future = CompletableFuture.supplyAsync(() -> userService.list());

        //从文件中读取数据
        FileReader fileReader = null;
        BufferedReader br = null;
        try {
//            fileReader = new FileReader("C:\\Users\\86184\\Desktop\\acm\\zzuliAcm\\src\\main\\resources\\py\\problem_set_label.txt");
            fileReader = new FileReader("/root/zzuliAcm/py/problem_set_label.txt");
//            if (fileReader == null) {
//                return false;
//            }
            br = new BufferedReader(fileReader);
            String line = null;
            StringBuilder str = new StringBuilder();
            while((line = br.readLine())!=null) {
                str.append(line);
            }

            map = JSONObject.parseObject(str.toString(), TreeMap.class);
            redisTemplate.opsForValue().set("problems_set", map);

//            users = future.get();
        } catch (IOException e) {
            log.warn("读取文件这里出错啦.... error: " + e);
            return false;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;

    }

    private boolean createInfoToRedis(Map<String, Integer> map, List<User> users) {
        if (users == null || map == null) return false;
        users.parallelStream().forEach(user -> {
            String key = "User" + user.getStuId();
            BoundHashOperations<String, String, Object> ops = redisTemplate.boundHashOps(key);
            Map<String, Object> entries = ops.entries();

            map.forEach((k, v) -> {
                if (entries.get(k) == null) {
                    entries.put(k, 0.0);
                }
            });
            entries.put("lastSubmitAt", LocalDateTime.now().toString());

            ops.putAll(entries);
        });

        return true;
    }


}
