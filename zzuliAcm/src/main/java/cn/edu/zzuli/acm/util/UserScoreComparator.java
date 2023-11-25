package cn.edu.zzuli.acm.util;

import cn.edu.zzuli.acm.to.RankTo;

import java.util.Comparator;

public class UserScoreComparator implements Comparator<RankTo> {

    /**
     * 如果返回 负数说明 o1 < o2
     * 如果返回 0 说明 o1 = o2
     * 如果返回 正数 说明 o1 > o2
     *
     * 而我们现在想要的效果是 按照 总分进行降序排序
     * 如果总分相同，则判断则按照时间升序排列，时间小的在前
     * @param o1
     * @param o2
     * @return 当方法的返回值大于0的时候就将数组的前一个数和后一个数做交换
     */
    @Override
    public int compare(RankTo o1, RankTo o2) {

        //因为是升序，所以如果总分高，那么它需要在前边
        if (o1.getTotalScore() < o2.getTotalScore()) {
            //返回1，让o2 与 o1进行位置交换
            return 1;
        }else if (o1.getTotalScore() > o2.getTotalScore()) {
            //这个时候我们无需交换位置
            return -1;
        }else {
            //如果两个值相等，这个时候我们要判断 时间了，由于我们直接将时间存成了一个字符串格式
            //所以这里我们直接比较的是字符串
            //如果o1 比 o2小的话，返回的是 -1，因为我们是升序，所以无需位置交换
            return o1.getTotalScore().compareTo(o2.getTotalScore());
        }
    }

}
