package cn.edu.zzuli.acm.service.impl;

import cn.edu.zzuli.acm.enums.RankConst;
import cn.edu.zzuli.acm.service.ICPCRankService;
import cn.edu.zzuli.acm.to.ICPCRankTo;
import cn.edu.zzuli.acm.vo.ICPCRankVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ICPCRankServiceImpl implements ICPCRankService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public ICPCRankVo getICPCTeamRank() {

        List<ICPCRankTo> icpcRanking
                = (List<ICPCRankTo>) redisTemplate.opsForValue().get(RankConst.ICPC_INFO.KEY());

        if (icpcRanking == null) return new ICPCRankVo();

        return new ICPCRankVo().setIcpcRanks(icpcRanking)
                .setTotal(icpcRanking.size());
    }
}
