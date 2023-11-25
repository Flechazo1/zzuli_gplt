package cn.edu.zzuli.acm.to;

import cn.edu.zzuli.acm.vo.UserScore;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class UserScoreTO {

    private List<UserScore> userScores;

    private Double primaryscore;

    private Double advancescore;

    private Double seniorscore;

    private Integer total;
}
