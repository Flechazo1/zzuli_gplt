package cn.edu.zzuli.acm.util;

import cn.edu.zzuli.acm.entity.Team;

import java.util.Comparator;

public class TeamScoreComparator implements Comparator<Team> {


    @Override
    public int compare(Team o1, Team o2) {
        //因为是升序，所以如果总分高，那么它需要在前边
        if (o1.getTotlescore() < o2.getTotlescore()) {
            //返回1，让o2 与 o1进行位置交换
            return 1;
        }else if (o1.getTotlescore() > o2.getTotlescore()) {
            //这个时候我们无需交换位置
            return -1;
        }else {
            //如果两个值相等，这个时候我们要判断 时间了，由于我们直接将时间存成了一个字符串格式
            //所以这里我们直接比较的是字符串
            //如果o1 比 o2小的话，返回的是 -1，因为我们是升序，所以无需位置交换
//            return o1.getLastSubmitAt().compareTo(o2.getLastSubmitAt());
            return 0;
        }
    }
}
