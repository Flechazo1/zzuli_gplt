package cn.edu.zzuli.acm.excel;

import cn.edu.zzuli.acm.entity.Team;
import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.mapper.TeamMapper;
import cn.edu.zzuli.acm.mapper.UserMapper;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ExcelDataListener extends AnalysisEventListener<ExcelData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelDataListener.class);

    private TeamMapper teamMapper;

    private UserMapper userMapper;

    /**
     * 如果使用了spring,请使用这个构造方法。每次创建Listener的时候需要把spring管理的类传进来
     */
    public ExcelDataListener(TeamMapper teamMapper, UserMapper userMapper) {
        this.teamMapper = teamMapper;
        this.userMapper = userMapper;
    }

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 5;
    List<ExcelData> list = new ArrayList<ExcelData>();
    List<User> students = new ArrayList<>();
    Team curTeam = new Team();
    int index = 1;

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param data
     *            one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param context
     */
    @Override
    public void invoke(ExcelData data, AnalysisContext context) {
        JSONObject jsonData = JSONObject.parseObject(JSON.toJSONString(data));
        //因为是合并单元格，excel确实一行行读取的，所以只有合并那几列的第一行有值，其余为空

        if (jsonData.getString("teamName") != null) {
            curTeam = new Team();
            String curTeamName = jsonData.getString("teamName");
            curTeam.setTeamname(curTeamName.trim());
            //并且此时可以取添加一条队伍数据到数据库中！
            teamMapper.insert(curTeam);
            //解析队员信息
            User stu = getStudentInfo(jsonData);
            stu.setTeamid(curTeam.getId());
            students.add(stu);
        }else {
            //接下来就都会是队伍里的成员了
            User stu = getStudentInfo(jsonData);
            stu.setTeamid(curTeam.getId());
            students.add(stu);
        }

        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (students.size() >= BATCH_COUNT) {
            saveData();
//            students.forEach(System.out::println);
            // 存储完成清理 list
            students.clear();
        }
    }

    private void saveData() {
        //这里注入service的话可以直接批量插入的，草率了
        //如果后期更改数据量的话，建议别这样干，但是目前我们也就5条数据，这样做也没什么不妥的
        students.forEach(student -> userMapper.insert(student));
    }

    private User getStudentInfo(JSONObject jsonData) {
        return new User().setStuId(jsonData.getString("stuNumber"))
                .setGender(jsonData.getString("gender"))
                .setUsername(jsonData.getString("stuName"));
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        LOGGER.info("所有数据解析完成！");
    }
}
