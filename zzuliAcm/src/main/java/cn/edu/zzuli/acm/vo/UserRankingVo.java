package cn.edu.zzuli.acm.vo;

import cn.edu.zzuli.acm.to.RankTo;
import lombok.Data;
import org.apache.poi.ss.formula.functions.Rank;

import java.util.List;

@Data
public class UserRankingVo {
    private List<RankTo> ranks;
    private Integer total;
}
