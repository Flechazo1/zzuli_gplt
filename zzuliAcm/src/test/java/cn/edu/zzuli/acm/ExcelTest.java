package cn.edu.zzuli.acm;

import cn.edu.zzuli.acm.excel.ExcelData;
import cn.edu.zzuli.acm.excel.ExcelDataListener;
import cn.edu.zzuli.acm.entity.Team;
import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.mapper.TeamMapper;
import cn.edu.zzuli.acm.mapper.UserMapper;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
@SuppressWarnings("unchecked")
@SpringBootTest(classes = AcmApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ExcelTest {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Autowired
    UserMapper userMapper;

    @Autowired
    TeamMapper teamMapper;


    @Test
    void testExcel() throws FileNotFoundException {

        List<Team> teams = (List<Team>) redisTemplate.opsForValue().get("teamRanking");

        List<Map<String, Object>> excelData = new ArrayList<>(teams.size());

        AtomicInteger n = new AtomicInteger();
        teams.forEach( team -> {
            n.getAndIncrement();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("rank", n.get());
            map.put("theClass", team.getThe_class());
            map.put("teamName", team.getTeamname());

            //查询该队队员
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getTeamid, team.getId());
            List<User> users = userMapper.selectList(wrapper);
            AtomicInteger i = new AtomicInteger(1);
            users.forEach( user -> {
                map.put("user"+ i.getAndIncrement(), user.getUsername());
            });

            excelData.add(map);
        });


//        excelData.forEach(System.out::println);


//         模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
        String templateFileName = "src/main/resources/excel/team-template.xlsx";

        // 方案1 根据对象填充
        String fileName = "01-" + System.currentTimeMillis() + ".xlsx";

        EasyExcel.write(fileName).withTemplate(templateFileName).sheet().doFill(excelData);
    }


    @Test
    void readExcel() {
        String fileName = "2021天梯赛报名表.xlsx";

        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, ExcelData.class, new ExcelDataListener(teamMapper, userMapper))
                .extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
    }

}
