package cn.edu.zzuli.acm.service;

import cn.edu.zzuli.acm.to.ICPCRankTo;
import cn.edu.zzuli.acm.to.RankTo;
import com.alibaba.fastjson.JSONObject;

import java.util.List;
import java.util.Map;

public interface RankJsonService {

    boolean parseJsonInfoToDB(List<JSONObject> list);

    //从文件中获取数据
    void getRankJsonInfo();

    String initRankJsonFromTheFile();

    String getRankJsonFromAfterTheId(String id, String limit);

    void fixScore();

    List<RankTo> getRankingFromRedis();

    List<ICPCRankTo> getICPCRankFromRedis();

    public Map<String,? extends Object> getFirstSubmit(List<ICPCRankTo> icpcRankTos);
}
