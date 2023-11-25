package cn.edu.zzuli.acm.to;

import lombok.Data;

import java.util.TreeMap;

/**
 * 虽说和 天梯赛共享 redis中的同一个key
 * 但是，并没有三个阶段分。
 * 所以还是单独建一个类来区分，这样更好一点
 */
@Data
public class ICPCRankTo {

    private Integer rank;
    private Integer solvingTime;
    private Integer totalScore;
    private StudentUser user;
    private TreeMap<String, Object> problemScores;
    //ac通过的个数。
    private Integer acCount;
    //开始的时间
    private String startAt;

}
