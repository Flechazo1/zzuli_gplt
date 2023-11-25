package cn.edu.zzuli.acm.enums;

public enum JobConst {
    //天梯赛排行榜的 定时任务开启或者关闭的状态位
    SCORE_JOB("scoreJob"),
    //爬取提交记录的定时任务 开启或者关闭的状态位
    PROBLEM_JOB("problemSubmitJob"),
    //iCPC排行榜定时任务 开启或者关闭的状态位
    ICPCRANK_JOB("icpcRankJob");

    private String key;

    JobConst(String key){
        this.key = key;
    }

    public String KEY() {
        return key;
    }
}
