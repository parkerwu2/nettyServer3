package com.example.demo.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jingzhi.wu on 2018/7/19.
 */
class Data{
    public int firstN = 3;
    // key 统计项目,  values: 改月改项目总支出
    public Map<String, BigDecimal> datas = new HashMap();
    public int month;
    public BigDecimal sum;
    public void output(){
        List<Map.Entry<String, BigDecimal>> list = new ArrayList<Map.Entry<String, BigDecimal>>(datas.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, BigDecimal>>() {
            @Override
            public int compare(Map.Entry<String, BigDecimal> o1, Map.Entry<String, BigDecimal> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        System.out.println("month:" + (month + 1) + "'s top " + firstN + " paid are:");
        for (int j = 0; j < firstN; j++){
            System.out.println(list.get(j).getKey() + ":" + list.get(j).getValue());
        }
        System.out.println("*********************");
    }
}

public class ExcelAccount {
    public static List<Map.Entry<String, BigDecimal>> mapToBig(Map<String, BigDecimal> map){
        List<Map.Entry<String, BigDecimal>> list = new ArrayList<Map.Entry<String, BigDecimal>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, BigDecimal>>() {
            @Override
            public int compare(Map.Entry<String, BigDecimal> o1, Map.Entry<String, BigDecimal> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        return list;
    }
    public static void main(String[] args) throws IOException {
        File f = new File("D:\\2018.xlsx");
        FileInputStream inputStream = new FileInputStream(f);
        MultipartFile file = new MockMultipartFile(f.getName(), inputStream);
//        List<String[]> excels = POIUtil.readExcel(file);
//        if (!CollectionUtils.isEmpty(excels)){
//
//        }
        int month = 6; //统计多少月份
        Workbook workbook = POIUtil.getWorkBook(file);
        List<Data> datas = new ArrayList<>();
        FormulaEvaluator formulaEvaluator = new XSSFFormulaEvaluator((XSSFWorkbook) workbook);
        if (workbook != null){
            for (int sheetNum = 0; sheetNum < month; sheetNum++){
                Data data = new Data();
                data.month = sheetNum;
                //获得当前sheet工作表
                Sheet sheet = workbook.getSheetAt(sheetNum);
                if(sheet == null){
                    continue;
                }
                //获得当前行 第一行是0
                for (int i = 2; i <= 28; i++){
                    Row row = sheet.getRow(i);
                    if(row == null){
                        continue;
                    }
                    // 第一列是0
                    Cell key = row.getCell(32);
                    Cell value = row.getCell(33);
//                    String v = value.getStringCellValue();
                    String v = getCellValueFormula(value, formulaEvaluator);
                    BigDecimal bv = BigDecimal.valueOf(Double.valueOf(v));
                    bv = bv.setScale(2, BigDecimal.ROUND_HALF_UP);
                    data.datas.put(key.getStringCellValue(), bv);
                }
                // 总支出
                Row sumrow = sheet.getRow(29);
                Cell s = sumrow.getCell(33);
                String sv = getCellValueFormula(s, formulaEvaluator);
                BigDecimal sb = BigDecimal.valueOf(Double.valueOf(sv));
                sb = sb.setScale(2, BigDecimal.ROUND_HALF_UP);
                data.sum = sb;
                datas.add(data);
            }
        }
        //开始统计
        // 每个月开销前3的项目
        // 6个月各个项目的总开销，并且按照开销降序排列
        Map<String, BigDecimal> sum = new HashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Data data : datas){
            total = total.add(data.sum);
            data.output();
            for (Map.Entry<String, BigDecimal> entry : data.datas.entrySet()){
                String key = entry.getKey();
                BigDecimal value = entry.getValue();

                if (sum.containsKey(key)){
                    sum.put(key, sum.get(key).add(value));
                } else {
                    sum.put(key, value);
                }
            }
        }

        List<Map.Entry<String, BigDecimal>> sumList = mapToBig(sum);
        System.out.println("*****sum result:*****");
        for (int j = 0; j < sumList.size(); j++){
            System.out.println(sumList.get(j).getKey() + ":" + sumList.get(j).getValue());
        }
        System.out.println("*******" + month + " month cost in all is:" + total);
        System.out.println("*****sum end ********");

    }

    public static String getCellValueFormula(Cell cell, FormulaEvaluator formulaEvaluator) {
        if (cell == null || formulaEvaluator == null) {
            return null;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
            return String.valueOf(formulaEvaluator.evaluate(cell).getNumberValue());
        }
        return getCellValue(cell);
    }

    public static String getCellValue(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getRichStringCellValue().getString().trim();
            case Cell.CELL_TYPE_NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//非线程安全
                    return sdf.format(cell.getDateCellValue());
                } else {
                    return String.valueOf(cell.getNumericCellValue());
                }
            case Cell.CELL_TYPE_BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case Cell.CELL_TYPE_FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
}
