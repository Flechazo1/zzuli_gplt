package cn.edu.zzuli.acm.enums;

public enum InfoConst {
    //天梯赛中的标准分
    STANDS("stands"),
    //当前赛制是否为 ICPC赛制
    IS_ICPC("isICPC"),
    IS_START("isStart"),
    //当前比赛的 题目信息
    PROBLEM_SET("problems_set");
    private String key;

    InfoConst(String key) {
        this.key = key;
    }

    public String KEY() {
        return key;
    }
}
