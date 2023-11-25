package cn.edu.zzuli.acm.service;

import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.vo.UserRankingVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
public interface UserService extends IService<User> {

    UserRankingVo getUserScore(Integer page, Integer size);

}
