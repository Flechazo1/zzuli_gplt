package cn.edu.zzuli.acm.service.impl;

import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.mapper.StandsMapper;
import cn.edu.zzuli.acm.service.StandsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

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
public class StandsServiceImpl extends ServiceImpl<StandsMapper, Stands> implements StandsService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public List<Stands> getNowStands() {
        return this.list();
    }

    @Override
    public boolean updateStands(Stands stands) {
        //将新的数据加入到redis
        redisTemplate.opsForValue().set("stands", stands);
        return true;
    }

    @Override
    public Stands getStands() {
        Stands stands = (Stands) redisTemplate.opsForValue().get("stands");
        return stands;
    }
}
