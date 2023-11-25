package cn.edu.zzuli.acm.service;

import cn.edu.zzuli.acm.entity.Stands;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
public interface StandsService extends IService<Stands> {

    List<Stands> getNowStands();

    boolean updateStands(Stands stands);

    Stands getStands();
}
