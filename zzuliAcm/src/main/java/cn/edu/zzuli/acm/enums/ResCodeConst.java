package cn.edu.zzuli.acm.enums;

public enum ResCodeConst {
    // job
    JOB_START(204),
    JOB_STOP(206),

    // ws
    //心跳检测状态码
    HURT_CHECK(210),
    //广播状态码
    BROADCAST(212);

    private Integer code;

    ResCodeConst(Integer code){
        this.code = code;
    }

    public Integer CODE() {
        return code;
    }
}
