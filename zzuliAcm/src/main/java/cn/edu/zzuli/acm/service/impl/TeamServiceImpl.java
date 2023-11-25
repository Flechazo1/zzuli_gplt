package cn.edu.zzuli.acm.service.impl;

import cn.edu.zzuli.acm.entity.Stands;
import cn.edu.zzuli.acm.entity.Team;
import cn.edu.zzuli.acm.enums.JobConst;
import cn.edu.zzuli.acm.mapper.TeamMapper;
import cn.edu.zzuli.acm.service.TeamService;
import cn.edu.zzuli.acm.service.UserService;
import cn.edu.zzuli.acm.to.RankTo;
import cn.edu.zzuli.acm.util.TeamScoreComparator;
import cn.edu.zzuli.acm.util.UserScoreComparator;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
@SuppressWarnings("unchecked")
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private TeamService teamService;

    @Override
    public List<Team> getTeamScore() {
        boolean flag = true;
        Object teamUpdateFlag = redisTemplate.opsForValue().get("teamUpdateFlag");

        if ( teamUpdateFlag != null) {
            flag = (boolean) teamUpdateFlag;
        }else {
            log.warn("暂未有人提交");
            return null;
        }

        if (!flag) {
            List<Team> teams = (List<Team>) redisTemplate.opsForValue().get("teamRanking");
            if (teams != null) return teams;
        }
        List<Team> teams = this.list();
        List<RankTo> rankTos = (List<RankTo>) redisTemplate.opsForValue().get("userRanking");
        if(rankTos==null){
           return null;
        }
        Stands stands = (Stands) redisTemplate.opsForValue().get("stands");
        teams.parallelStream().forEach(team -> {
            String the_class = "未知班级";
            Integer teamId = team.getId();
            team.setPrimaryscore(0.0);
            team.setAdvancescore(0.0);
            team.setSeniorscore(0.0);
            List<RankTo> userScoreList = new ArrayList<>();
//            wrapper.eq("teamId", teamId);

            int n = 0;
            for(RankTo rankTo : rankTos){
                if (n == 10) break;

//                System.out.println(rankTo.getUser().getTeamId());
                //如果是同一个队伍，则开始组装啦
                  if (rankTo.getUser().getTeamId()==null){
                      System.out.println(rankTo.getUser().getId());
                      System.out.println(rankTo.getUser().getName());
                      System.out.println(rankTo.getUser().getStudentNumber());
                      System.out.println(rankTo.getUser().getTheClass());
                      continue;
                  }
                if(rankTo.getUser().getTeamId().equals(teamId)){
                    the_class = rankTo.getUser().getTheClass();
                    userScoreList.add(rankTo);
                    team.setPrimaryscore(team.getPrimaryscore() + rankTo.getPrimaryScore());
                    team.setAdvancescore(team.getAdvancescore() + rankTo.getAdvanceScore());
                    team.setSeniorscore(team.getSeniorscore() + rankTo.getSeniorScore());
                    n++;
                }
            }
            team.setTotlescore(team.getPrimaryscore());
            if(team.getPrimaryscore() >= (stands.getPrimaryscore()*10)){
                team.setTotlescore(team.getTotlescore() + team.getAdvancescore());
                if(team.getAdvancescore() >= (stands.getAdvancescore()*10)){
                    team.setTotlescore(team.getTotlescore() + team.getSeniorscore());
                }
            }
//            if(team.getPrimaryscore()>=600){
//                team.setTotlescore(team.getTotlescore() + team.getAdvancescore());
//                if (team.getAdvancescore() >=200){
//                    team.setTotlescore(team.getTotlescore() + team.getSeniorscore());
//                }
//            }
            team.setThe_class(the_class);
        });
        Collections.sort(teams, new TeamScoreComparator());
        double lastTotal = 0;
        int equalNumber= 0;
        int rank = 1;
        for (Team team : teams) {

            if (team.getTotlescore() != lastTotal) {
                rank += equalNumber;
                team.setRank(rank);
                rank += 1;
                equalNumber = 0;
            }else {
                equalNumber++;
                team.setRank(rank - 1);
            }
            lastTotal = team.getTotlescore();
        }
        redisTemplate.opsForValue().set("teamRanking", teams);
        //自己已经更新过数据了，那么把标志位设为false
        redisTemplate.opsForValue().set("teamUpdateFlag", false);
        return teams;
    }

    @Override
    public List<RankTo> getTeamScoreDetil(Integer teamid) {
        List<RankTo> rankTos = (List<RankTo>) redisTemplate.opsForValue().get("userRanking");
        if(rankTos==null){
            return null;
        }
        int n = 0;

        List<RankTo> userScoreList = new ArrayList<>();
        for(RankTo rankTo : rankTos){
            if (n == 10) break;
            if (rankTo.getUser().getTeamId()==null) continue;
            //如果是同一个队伍，则开始组装啦
            if(rankTo.getUser().getTeamId().equals(teamid)){
                userScoreList.add(rankTo);
                n++;
            }
        }
        Collections.sort(userScoreList, new UserScoreComparator());
        return userScoreList;
    }
}
