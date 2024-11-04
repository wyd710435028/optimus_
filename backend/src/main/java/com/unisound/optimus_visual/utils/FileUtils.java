package com.unisound.optimus_visual.utils;

import com.unisound.optimus_visual.modules.medicalrecord.model.ExportFormatedOrder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/**
 * 文件操作工具类
 */
public class FileUtils {

    public static void combineTheLastTwoColumnsInTxt(String txtUrl){
        StringBuffer sb = new StringBuffer();
        File file = new File(txtUrl);
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                if (split.length==3){
                    String split2 = split[2];
                    if (split[2].contains("；")){
                         split2 = split[2].replaceAll("；", "|");
                    }
                    String str = split[0]+"\t"+split[1]+"|"+split2;
                    sb.append(str);
                    sb.append("\n");
                }else {
                    sb.append(line);
                    sb.append("\n");
                }
            }
            //写入txt文件
            outputTxtByStringBuffer(sb,"D:\\combined.txt");
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将
     * @param stringBuffer
     * @param outPutUrl
     */
    public static void outputTxtByStringBuffer(StringBuffer stringBuffer,String outPutUrl){
        if (stringBuffer!=null&&stringBuffer.length()>0){
            File newFile = new File(outPutUrl);
            try {
                FileWriter fileWriter = new FileWriter(newFile);
                fileWriter.write(stringBuffer.toString());
                fileWriter.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void exportToExcel(HttpServletResponse response, List<ExportFormatedOrder> orders, String fileName) throws IOException {
        // 创建一个新的工作簿
        Workbook workbook = new XSSFWorkbook();
        // 创建一个工作表
        Sheet sheet = workbook.createSheet("医嘱信息");

        // 创建表头
        Row headerRow = sheet.createRow(0);
        String[] headers = {"流水号", "医嘱类型(临时/长期)", "医嘱ID", "医嘱内容", "云知声项目类别", "项目大类"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        // 填充数据
        int rowNum = 1;
        for (ExportFormatedOrder order : orders) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(order.getAdmissionId());
            row.createCell(1).setCellValue(order.getOrderType());
            row.createCell(2).setCellValue(order.getOrderId());
            row.createCell(3).setCellValue(order.getOrderContent());
            row.createCell(4).setCellValue(order.getYzsProjectType());
            row.createCell(5).setCellValue(order.getProjectCategories());
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName +"\"");
        workbook.write(response.getOutputStream());
        workbook.close();
        // 写入文件
//        try (FileOutputStream fileOut = new FileOutputStream(fileName)) {
//            workbook.write(fileOut);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                workbook.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
