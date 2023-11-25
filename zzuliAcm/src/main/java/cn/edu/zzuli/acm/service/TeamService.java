package cn.edu.zzuli.acm.service;

import cn.edu.zzuli.acm.entity.Team;
import cn.edu.zzuli.acm.to.RankTo;
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
public interface TeamService extends IService<Team> {

    List<Team> getTeamScore();

    List<RankTo> getTeamScoreDetil(Integer teamid);
}
