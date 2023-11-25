package cn.edu.zzuli.acm.to;

import lombok.Data;

import java.util.TreeMap;

/**
 * 天体赛的 排行榜 to
 * 用来 从 redis中读取 ranking_json 这个key的数据
 */
@Data
public class RankTo {

    private Integer rank;
    private Integer primaryScore = 0;
    private Integer advanceScore = 0;
    private Integer seniorScore = 0;
    private Integer totalScore;
    private StudentUser user;
    private TreeMap<String, Object> problemScores;

}
