package cn.edu.zzuli.acm.service;

import cn.edu.zzuli.acm.entity.Config;
import cn.edu.zzuli.acm.vo.ConfigVo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author geji
 * @since 2020-11-30
 */
public interface ConfigService extends IService<Config> {

    boolean updateAdminConfig(Config config);

    ConfigVo getAdminInfo();

    boolean initRedisInfo(String problemId);
}
