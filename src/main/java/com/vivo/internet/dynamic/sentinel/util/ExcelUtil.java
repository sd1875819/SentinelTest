package com.vivo.internet.dynamic.sentinel.util;


import com.alibaba.fastjson.JSONObject;
import com.vivo.internet.dynamic.sentinel.dto.CommentReason;
import com.vivo.internet.dynamic.sentinel.dto.CommentReasonData;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author 11123357
 * @Date 2022/11/16 18:14
 * @Version 1.0
 * 文件功能：解析一个含多个sheet的excel表格，将其解析成json数据：
 */


public class ExcelUtil {


    public static List<CommentReasonData> parseCommentReasonData(String filePath) throws Exception {
        File excelFile = new File(filePath);
        //先判断所给的地址对应的excel文件是否存在
        if (!excelFile.isFile() || !excelFile.exists()){
            System.out.println("file not exist !!!");
            return null;
        }
        //通过文件后缀判断文件是否是excel文件
        String[] split  = excelFile.getName().split("\\.");
        if(!split[1].equals("xlsx") && !split[1].equals("xls")){
            System.out.println("file suffix error :" + split[1]);
        }
        //开始解析excel文件的每一个sheet里的内容：
        List<CommentReasonData> results = new LinkedList<>();//定义excel文件解析结果的json存储对象
        FileInputStream fls = new FileInputStream(excelFile);
        Workbook wb = split[1].equals("xls") ? new HSSFWorkbook(fls) : new XSSFWorkbook(fls);
        for(int i = 0; i < wb.getNumberOfSheets(); i++){ //循环获取excel文件中每一个sheet的数据
            Sheet sheet = wb.getSheetAt(i);
            String scene = sheet.getSheetName();

            int lastRowIndex = sheet.getLastRowNum(); //获取当前sheet里有多少行数据
            if (lastRowIndex <= 0){
                System.out.println("sheet:" + scene + " rows num error");
                continue;
            }

            CommentReasonData reasonData = new CommentReasonData();
            reasonData.setTag(scene); //json结果中的tag参数存储sheet名称
            for (int j = 1; j <= lastRowIndex; j++){
                Row row = sheet.getRow(j);  //获取当前sheet中一整行的数据
                //获取excel中一行数据中的每一列具体数据
                Long appId = Double.valueOf(row.getCell(0).toString()).longValue();
                Long reasonId = Double.valueOf(row.getCell(1).toString()).longValue();
                String reasonStr = row.getCell(2).toString().replaceAll("\"", "");
                String attributes = row.getCell(3).toString();
                //存储数据
                CommentReason reason = new CommentReason();
                reason.setAppId(appId.toString());
                reason.setReasonId(reasonId.toString());
                reason.setReason(reasonStr);
                reason.setAttributes(attributes);

                reasonData.addData(reason);
            }
            results.add(reasonData);
        }
        return results;
    }

    public static void main(String[] args) throws Exception {
        List<CommentReasonData> results = parseCommentReasonData("D:\\评论推荐理由实验组配置.xls");
        System.out.println(JSONObject.toJSONString(results));
    }

}
