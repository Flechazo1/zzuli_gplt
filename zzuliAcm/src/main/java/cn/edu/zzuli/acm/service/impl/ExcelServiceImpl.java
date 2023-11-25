package cn.edu.zzuli.acm.service.impl;

import cn.edu.zzuli.acm.entity.Team;
import cn.edu.zzuli.acm.entity.User;
import cn.edu.zzuli.acm.excel.ExcelData;
import cn.edu.zzuli.acm.excel.ExcelDataListener;
import cn.edu.zzuli.acm.mapper.TeamMapper;
import cn.edu.zzuli.acm.mapper.UserMapper;
import cn.edu.zzuli.acm.service.ExcelService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.enums.CellExtraTypeEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Resource
    UserMapper userMapper;

    @Resource
    TeamMapper teamMapper;


    @Override
    public boolean exportExcelFromRes(HttpServletResponse response) {
        try {
            response.setHeader("Content-disposition", "attachment; filename=" + "team-ranking.xlsx");
            response.setContentType("application/msexcel;charset=UTF-8");//设置类型
            OutputStream outputStream = response.getOutputStream();

            return dataToExcel(outputStream);
        } catch (IOException e) {
            log.info("到处excel出错啦...");
            return false;
        }
    }

    @Override
    public boolean readExcelDataToDB(MultipartFile file) throws IOException {
        //这里改成接收的 file 文件

        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(file.getInputStream(), ExcelData.class, new ExcelDataListener(teamMapper, userMapper))
                .extraRead(CellExtraTypeEnum.MERGE).sheet().doRead();
        return false;
    }

    private boolean dataToExcel(OutputStream outputStream) throws IOException {

        List<Team> teams = (List<Team>) redisTemplate.opsForValue().get("teamRanking");
        if (teams == null) return false;

        List<Map<String, Object>> excelData = new ArrayList<>(teams.size());

        AtomicInteger n = new AtomicInteger();
        teams.forEach( team -> {
            n.getAndIncrement();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("rank", team.getRank());
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

        InputStream templateFile = this.getClass().getClassLoader()
                .getResourceAsStream("excel/team-template.xlsx");
//         模板注意 用{} 来表示你要用的变量 如果本来就有"{","}" 特殊字符 用"\{","\}"代替
//        String templateFileName = "src/main/resources/excel/team-template.xlsx";
        if (templateFile == null) {
            System.out.println("空文件哦");
            return false;
        }

        //根据map填充
        EasyExcel.write(outputStream)
                .withTemplate(templateFile).sheet().doFill(excelData);
        return true;
    }


}
