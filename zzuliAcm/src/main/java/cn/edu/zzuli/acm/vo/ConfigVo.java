package cn.edu.zzuli.acm.vo;

import cn.edu.zzuli.acm.entity.Config;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConfigVo {
    private Config basicInfo;
    private boolean isStart;
    private boolean isICPC;
    private boolean scoreJob;
    private boolean problemJob;
    private boolean icpcRankJob;
}
