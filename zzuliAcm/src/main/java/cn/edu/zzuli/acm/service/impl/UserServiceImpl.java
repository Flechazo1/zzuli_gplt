package cn.edu.zzuli.acm.service.impl;

import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.enums.RankConst;
import cn.edu.zzuli.acm.mapper.UserMapper;
import cn.edu.zzuli.acm.service.StandsService;
import cn.edu.zzuli.acm.service.UserService;
import cn.edu.zzuli.acm.to.RankTo;
import cn.edu.zzuli.acm.vo.UserRankingVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StandsService standsService;

    @Override
    public UserRankingVo getUserScore(Integer page, Integer size) {

        List<RankTo> rankTos = (List<RankTo>) redisTemplate.opsForValue().get(RankConst.USER_RANK.KEY());
        if (rankTos == null) return null;
        UserRankingVo to = new UserRankingVo();
        to.setRanks(rankTos);
        to.setTotal(rankTos.size());
        List<RankTo> newUserRankList = new ArrayList<>();
        //手动写的假分页，懂得都懂，看起来快... 实际上。
        int begin = (page-1) * size;
        int end = (begin+size);

        if (begin > to.getTotal()) {
            return null;
        }
        if (end > to.getTotal()) {
            end = to.getTotal();
        }
        for(int i = begin; i < end; i++){
            newUserRankList.add(rankTos.get(i));
//            System.out.println(rankTos.get(i));
        }
        to.setRanks(newUserRankList);
        return to;
    }
}
