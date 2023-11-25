package cn.edu.zzuli.acm.mapper;

import cn.edu.zzuli.acm.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
