package cn.edu.zzuli.acm.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ExcelData {

    @ExcelProperty("队名")
    private String teamName;

    @ExcelProperty("学号")
    private String stuNumber;

    @ExcelProperty("队员")
    private String stuName;

    @ExcelProperty("性别")
    private String gender;

}
