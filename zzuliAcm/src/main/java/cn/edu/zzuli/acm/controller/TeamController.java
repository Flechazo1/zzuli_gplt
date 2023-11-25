package cn.edu.zzuli.acm.controller;


import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.entity.Team;
import cn.edu.zzuli.acm.service.TeamService;
import cn.edu.zzuli.acm.to.RankTo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
@RestController
@RequestMapping("/team")
@Api("团队api")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @GetMapping("/getTeamScore")
    @ApiOperation("获取团队排名")
    public R getTeamScore(){
        List<Team> teamScores = teamService.getTeamScore();
        return R.success().add("teamScores", teamScores);
    }

    @GetMapping("/getTeamScoreDetil/{teamid}")
    @ApiOperation("获取团队成员分数信息详情")
    public R getTeamScoreDetil(@PathVariable Integer teamid){
        List<RankTo> userScores = teamService.getTeamScoreDetil(teamid);
        return R.success().add("scoresDetail", userScores);
    }
}

