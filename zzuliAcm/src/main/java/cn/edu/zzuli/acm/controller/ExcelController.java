package cn.edu.zzuli.acm.controller;

import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RequestMapping("/excel")
@RestController
public class ExcelController {

    @Autowired
    ExcelService excelService;


    @GetMapping("/team/export")
    @ApiOperation("导入团队排行的数据到excel里")
    public R importTheExcel(HttpServletResponse response) {
        if (!excelService.exportExcelFromRes(response)){
            response.setContentType("application/json");
            response.setHeader("Content-disposition", "");
            return R.error().appendMsg("导出excel失败");
        }
        return null;
    }

}
