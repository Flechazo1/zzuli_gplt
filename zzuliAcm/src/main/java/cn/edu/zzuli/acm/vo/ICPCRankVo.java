package cn.edu.zzuli.acm.vo;

import cn.edu.zzuli.acm.to.ICPCRankTo;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ICPCRankVo {
    private List<ICPCRankTo> icpcRanks;
    private Integer total;
}
