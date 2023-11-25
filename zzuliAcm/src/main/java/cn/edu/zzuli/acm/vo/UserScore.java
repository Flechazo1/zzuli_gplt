package cn.edu.zzuli.acm.vo;

import cn.edu.zzuli.acm.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

@Data
public class UserScore {

    private User user;

    private Double primaryScore;

    private Double advanceScore;

    private Double seniorScore;

    private List<ProblemItemScore> primaryScoreItems;

    private List<ProblemItemScore> advanceScoreItems;

    private List<ProblemItemScore> seniorScoreItems;

    //总分数
    private Double sumScore;

    //最新的提交时间
    @JsonIgnore
    private String lastSubmitAt;

}
