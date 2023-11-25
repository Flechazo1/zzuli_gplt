package cn.edu.zzuli.acm.service;

import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.entity.Result;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author geji
 * @since 2020-12-21
 */
public interface ResultService extends IService<Result> {

    boolean saveInfo(Integer id);

    R getBasicInfoById(Integer id);

    R saveCompletionBasicInfo(Result result);
}
