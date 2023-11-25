package cn.edu.zzuli.acm.entity;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class UserSumScore {

    private User user;

    private Map<String, Double> score;
}
