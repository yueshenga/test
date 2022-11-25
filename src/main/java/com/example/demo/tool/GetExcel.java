package com.example.demo.tool;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;

public class GetExcel {

    //读取EXCEL，xls格式
    @SuppressWarnings({"resource", "deprecation"})
    public static String getTextFromExcel(String filePath) {
        StringBuilder buff = new StringBuilder();
        try {
            // 创建对Excel工作簿文件的引用
            HSSFWorkbook wb = new HSSFWorkbook(new FileInputStream(filePath));
            // 创建对工作表的引用。
            for (int numSheets = 0; numSheets < wb
                    .getNumberOfSheets(); numSheets++) {
                if (null != wb.getSheetAt(numSheets)) {
                    HSSFSheet aSheet = wb.getSheetAt(numSheets);// 获得一个sheet
                    for (int rowNumOfSheet = 0; rowNumOfSheet <= aSheet
                            .getLastRowNum(); rowNumOfSheet++) {
                        if (null != aSheet.getRow(rowNumOfSheet)) {
                            HSSFRow aRow = aSheet.getRow(rowNumOfSheet); // 获得一个行
                            for (int cellNumOfRow = 0; cellNumOfRow <= aRow
                                    .getLastCellNum(); cellNumOfRow++) {
                                if (null != aRow.getCell(cellNumOfRow)) {
                                    HSSFCell aCell = aRow.getCell(cellNumOfRow);// 获得列值
                                    switch (aCell.getCellType()) {
                                        case HSSFCell.CELL_TYPE_FORMULA :
                                            break;
                                        case HSSFCell.CELL_TYPE_NUMERIC :
                                            buff.append(
                                                            aCell.getNumericCellValue())
                                                    .append('\t');
                                            break;
                                        case HSSFCell.CELL_TYPE_STRING :
                                            buff.append(
                                                            aCell.getStringCellValue())
                                                    .append('\t');
                                            break;
                                    }
                                }
                            }
                            buff.append('\n');
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buff.toString();
    }

    //读取EXCEL，xlxs格式
    @SuppressWarnings("deprecation")
    public static String getTextFromExcel2007(String filePath) {
        StringBuilder buff = new StringBuilder();
        try {
            // 创建对Excel工作簿文件的引用
            @SuppressWarnings("resource")
            XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(filePath));
            // 创建对工作表的引用。
            for (int numSheets = 0; numSheets < wb
                    .getNumberOfSheets(); numSheets++) {
                if (null != wb.getSheetAt(numSheets)) {
                    XSSFSheet aSheet = wb.getSheetAt(numSheets);// 获得一个sheet
                    for (int rowNumOfSheet = 0; rowNumOfSheet <= aSheet
                            .getLastRowNum(); rowNumOfSheet++) {
                        if (null != aSheet.getRow(rowNumOfSheet)) {
                            XSSFRow aRow = aSheet.getRow(rowNumOfSheet); // 获得一个行
                            for (int cellNumOfRow = 0; cellNumOfRow <= aRow
                                    .getLastCellNum(); cellNumOfRow++) {
                                if (null != aRow.getCell(cellNumOfRow)) {
                                    XSSFCell aCell = aRow.getCell(cellNumOfRow);// 获得列值
                                    switch (aCell.getCellType()) {
                                        case HSSFCell.CELL_TYPE_FORMULA :
                                            break;
                                        case HSSFCell.CELL_TYPE_NUMERIC :
                                            buff.append(
                                                            aCell.getNumericCellValue())
                                                    .append('\t');
                                            break;
                                        case HSSFCell.CELL_TYPE_STRING :
                                            buff.append(
                                                            aCell.getStringCellValue())
                                                    .append('\t');
                                            break;
                                    }
                                }
                            }
                            buff.append('\n');
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buff.toString();
    }

    public static void main(String[] args) {
        String textFromExcel2007 = getTextFromExcel2007("D:\\myworkplace\\test\\src\\main\\resources\\static\\other\\湖北省财政厅预算管理一体化项目人员基本信息统计表-万胜.xlsx");
        System.out.println(textFromExcel2007);
    }
}
