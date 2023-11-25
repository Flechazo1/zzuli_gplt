package cn.edu.zzuli.acm.service;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface ExcelService {

    boolean exportExcelFromRes(HttpServletResponse response);

    boolean readExcelDataToDB(MultipartFile file) throws IOException;

}
