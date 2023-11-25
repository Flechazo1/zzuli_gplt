
package cn.edu.zzuli.acm.to;

import cn.edu.zzuli.acm.entity.Team;

import cn.edu.zzuli.acm.vo.UserScore;
import lombok.Data;

import java.util.List;

@Data
public class TeamScoreTo {

    private Team team;

    private List<UserScore> userScores;

    private Double sumScore;
}

