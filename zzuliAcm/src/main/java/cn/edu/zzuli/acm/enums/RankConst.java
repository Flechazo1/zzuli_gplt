package cn.edu.zzuli.acm.enums;

public enum RankConst {
    //天梯赛的个人排行榜
    USER_RANK("userRanking"),
    //天梯赛的团队排行榜
    TEAM_RANK("teamRanking"),
    //ICPC 赛制的排行榜
    ICPC_INFO("icpcInfo"),
    //题目的第一个AC队伍信息
    FIRST_SUBMIT("firstAC");

    private String key;

    RankConst(String key) {
        this.key = key;
    }

    public String KEY() {
        return key;
    }


}
