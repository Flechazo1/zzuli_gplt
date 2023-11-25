package cn.edu.zzuli.acm.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Data
@Accessors(chain = true)
public class R {

    private Integer code;

    private String msg;

    private Map<String, Object> data = new HashMap<>();

    private static final String[] successMsg = {"🎂 来块蛋糕吗？",  "还挺行, 🎅🎁", "ekij [🍻]~(￣▽￣)~*", "啊这，🐮啊"};
    private static final String[] errorMsg = {"你这有点不行！ 👻",  "🐱-->‍🚀)) 猫都跟着火箭飞了", "跟我走一趟 啊👮‍", "???? 😕"};
    private static Random random = new Random();

    public static R success() {
        R res =new R();

        res.setCode(200).setMsg("success: 😎 " + successMsg[random.nextInt(4)] );
        return res;
    }

    public R add(String key, Object value) {
        this.getData().put(key, value);
        return this;
    }

    public static R error() {
        R res = new R();
        res.setCode(100).setMsg("error: " + errorMsg[random.nextInt(4)]);
        return res;
    }

    public R appendMsg(String msg) {
        this.msg += ", info:: " + msg;
        return this;
    }

}
