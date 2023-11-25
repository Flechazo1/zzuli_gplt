package cn.edu.zzuli.acm.service.impl;

import cn.edu.zzuli.acm.entity.*;
import cn.edu.zzuli.acm.enums.InfoConst;
import cn.edu.zzuli.acm.enums.JobConst;
import cn.edu.zzuli.acm.enums.RankConst;
import cn.edu.zzuli.acm.mapper.ConfigMapper;
import cn.edu.zzuli.acm.mapper.ResultMapper;
import cn.edu.zzuli.acm.service.ResultService;
import cn.edu.zzuli.acm.to.ICPCRankTo;
import cn.edu.zzuli.acm.to.RankTo;
import cn.edu.zzuli.acm.util.PythonExecuteUtils;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author geji
 * @since 2020-12-21
 */
@Service
@Slf4j
public class ResultServiceImpl extends ServiceImpl<ResultMapper, Result> implements ResultService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    ConfigMapper configMapper;

    /**
     * 将当前比赛的最终结果以json的格式保存在 数据库中。
     * @return
     */
    @Override
    @Transactional
    public boolean saveInfo(Integer id) {

        Result result = new Result();

        //获取当前题目集的id
        Map<String, Object> problems_set =
                (Map<String, Object>) redisTemplate.opsForValue().get(InfoConst.PROBLEM_SET.KEY());
        //将数据转化为json
        String problemInfo = JSONObject.toJSONString(problems_set);

        //设置当前比赛的题目信息
        result.setRankProblemsInfo(problemInfo);
        //设置题目集id
        result.setRankProblemId(PythonExecuteUtils.getProblemId());

        //判断当前是否为 icpc
        boolean isIcpc = (boolean) redisTemplate.opsForValue().get(InfoConst.IS_ICPC.KEY());
        if (isIcpc) {
            //当前是 ICPC赛制
            result.setIsIcpc(1);
            List<ICPCRankTo> ranking = (List<ICPCRankTo>) redisTemplate.opsForValue().get(RankConst.ICPC_INFO.KEY());
            //icpc赛制 都是团队赛，这里我们干脆直接放在团队info中好了。
            result.setRankTeamInfo(JSONObject.toJSONString(ranking));
        }else {
            //当前为天梯赛赛制、
            result.setIsIcpc(0);
            //获取团队排名和个人排名。
            List<RankTo> userRanking = (List<RankTo>) redisTemplate.opsForValue().get(RankConst.USER_RANK);
            List<Team>  teamRanking = (List<Team>) redisTemplate.opsForValue().get(RankConst.TEAM_RANK);
            result.setRankUserInfo(JSONObject.toJSONString(userRanking));
            result.setRankTeamInfo(JSONObject.toJSONString(teamRanking));

            //当前比赛的stands
            Stands stands = (Stands) redisTemplate.opsForValue().get(InfoConst.STANDS.KEY());
            result.setStands(JSONObject.toJSONString(stands));

        }

        //flush redis中的数据。
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);

        //再次初始化项目的基本信息。将所有数据恢复到项目启动时。
        initBasicInfo();

        if (id == null) {
            return this.save(result);
        }else {
            result.setId(id);
            return this.updateById(result);
        }
    }

    @Override
    public R getBasicInfoById(Integer id) {
        return null;
    }

    @Override
    public R saveCompletionBasicInfo(Result result) {
        System.out.println(result);
        //开启比赛标志位
        redisTemplate.opsForValue().set(InfoConst.IS_START.KEY(), true);
        //修改 sessionId; 293f7994-3054-4468-ae8c-6343f712caf2    1328513552276385792
        Config config = new Config().setSession(result.getSessionId())
                .setProblemid(result.getRankProblemId()).setId(1);
        configMapper.update(config, null);

        PythonExecuteUtils.updateTheSessionId(config.getSession());
        PythonExecuteUtils.updateTheProblemsId(config.getProblemid());

        //保存当前比赛机制。
        Integer isIcpc = result.getIsIcpc();
        if (isIcpc == 0) {
            redisTemplate.opsForValue().set(InfoConst.IS_ICPC.KEY(), false);
        }else if (isIcpc == 1){
            redisTemplate.opsForValue().set(InfoConst.IS_ICPC.KEY(), true);
        }

        if (this.save(result)) {
            return R.success().add("completion", result);
        }
        return R.error().appendMsg("save 失败");
    }


    private void initBasicInfo() {
        //判断比赛是不是已经开始了

        if (!redisTemplate.hasKey(InfoConst.IS_START.KEY())) {
            log.info(">>> 比赛处于关闭中...");
            //默认关闭
            redisTemplate.opsForValue().set(InfoConst.IS_START.KEY(), false);
        }else {
            boolean isStart = (boolean) redisTemplate.opsForValue().get(InfoConst.IS_START.KEY());
            if (isStart) log.info("<<< 比赛正在进行中...");
            else log.info(">>> 比赛处于关闭中...");
        }

        log.info("天梯赛比赛Stands初始化：初级：80，进阶：40");
        if (!redisTemplate.hasKey(InfoConst.STANDS.KEY())) {
            Stands stands = new Stands(1, 80.0, 40.0);
            redisTemplate.opsForValue().set(InfoConst.STANDS.KEY(), stands);
        }

        //默认不是ICPC赛制
        if (!redisTemplate.hasKey(InfoConst.IS_ICPC.KEY())) {
            redisTemplate.opsForValue().set(InfoConst.IS_ICPC.KEY(), false);
            log.info("当前赛制为：天梯赛");
        }else {
            Boolean isICPC = (Boolean) redisTemplate.opsForValue().get(InfoConst.IS_ICPC.KEY());
            if (isICPC) {
                log.info("当前赛制为：ICPC赛制");
            }else {
                log.info("当前赛制为：天梯赛");
            }
        }

        //对定时任务标识进行初始化
        //默认设置三个定时任务为关闭状态
        redisTemplate.opsForValue().set(JobConst.SCORE_JOB.KEY(), false);
        redisTemplate.opsForValue().set(JobConst.PROBLEM_JOB.KEY(), false);
        redisTemplate.opsForValue().set(JobConst.ICPCRANK_JOB.KEY(), false);

        //打印题目集信息
        Object problems_set = redisTemplate.opsForValue().get(InfoConst.PROBLEM_SET.KEY());
        log.info("当前 problems_set 情况 --->>> " + problems_set);

        //初始化 session 和 problemid
        Config config = configMapper.selectById(1);
        PythonExecuteUtils.updateTheSessionId(config.getSession());
        PythonExecuteUtils.updateTheProblemsId(config.getProblemid());
        log.info("当前info 情况 --->>> session : " + config.getSession() + ", problem_id: " + config.getProblemid());
    }
}
