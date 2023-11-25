package cn.edu.zzuli.acm;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MybatiPlusGeneratorTest {

    @Test
    void TestGenerator() {
        //1.创建代码生成器
        AutoGenerator mpg = new AutoGenerator();


        //2.全局配置
        GlobalConfig gc = new GlobalConfig();
        //获取当前项目的路径
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath + "/src/main/java");
        gc.setAuthor("geji");
        //生成后是否打开资源管理器
        gc.setOpen(false);
        //生成文件的时候是否重新覆盖
        gc.setFileOverride(false);
        //设置主键策略
        gc.setIdType(IdType.AUTO);

        //设置Service首字母去除I
        gc.setServiceName("%sService");
        //设置日期类型
        gc.setDateType(DateType.TIME_PACK);
        //实体属性 Swagger2 注解
        gc.setSwagger2(true);

        mpg.setGlobalConfig(gc);


        //3.数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://127.0.0.1:3306/zzuliRank?serverTimezone=Asia/Shanghai&useUnicode=yes");
        // dsc.setSchemaName("public");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("suzy");
        mpg.setDataSource(dsc);


        // 包配置
        PackageConfig pc = new PackageConfig();
//        pc.setModuleName(scanner("模块名"));
        pc.setParent("cn.edu.zzuli.acm");
        pc.setEntity("entity");
        pc.setController("controller");
        pc.setService("service");
        pc.setMapper("mapper");

        mpg.setPackageInfo(pc);

        //策略配置
        StrategyConfig strategy = new StrategyConfig();
        //strategy.setInclude("xxx"+"_\\w*");//映射的表名
        strategy.setTablePrefix("rank_");//不生成表的前缀
        strategy.setNaming(NamingStrategy.underline_to_camel);//驼峰策略
        //strategy.setColumnNaming(NamingStrategy.underline_to_camel);

        //自动添加 lombok的注解
        strategy.setEntityLombokModel(true);
        strategy.setLogicDeleteFieldName("deleted");
        //去除boolean值的前缀
        //strategy.setEntityBooleanColumnRemoveIsPrefix(true);

        //生成自动填充
        TableFill createTime = new TableFill("add_time", FieldFill.INSERT);
//        TableFill updateTime = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        List<TableFill> tableFills = new ArrayList<>();
        tableFills.add(createTime);
//        tableFills.add(updateTime);
        strategy.setTableFillList(tableFills);

        //生成乐观锁的列，version 字段，这里我没有用，就注释掉
        //strategy.setVersionFieldName("version");

        //RestFul API
        strategy.setRestControllerStyle(true);
        //url 驼峰命名，转换为_
        strategy.setControllerMappingHyphenStyle(true);

        mpg.setStrategy(strategy);

        //执行
        mpg.execute();
    }

}
