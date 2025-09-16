package org.example;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReadCourses {
    
    public static List<Course> getCourses(String xlsDir) {
        List<Course> courses = readCourses(xlsDir);

        // for (Course c : courses) {
        //     System.out.println(Arrays.asList(c.getFormattedSched()));
        // }
        return courses;
    }

    public static List<Course> readCourses(String dir) {
        File directory = new File(dir);
        File[] files = directory.listFiles();
        List<Course> finalDb = new ArrayList<>();

        if (files == null) {
            System.err.println("Nothing at: " + dir);
            return finalDb;
        }

        for (File file : files) {

            List<Course> fileCourses = readExcelFile(file);
            finalDb.addAll(fileCourses);
        }
        
        return finalDb;
    }

    private static List<Course> readExcelFile(File file) {
        List<Course> courses = new ArrayList<>();
        
        try (FileInputStream fis = new FileInputStream(file)) {
            Workbook workbook;

            workbook = new HSSFWorkbook(fis);
            Sheet sheet = workbook.getSheetAt(0);
            boolean skipHeader = true;
            
            for (Row row : sheet) {
                if (skipHeader) {
                    skipHeader = false;
                    continue;
                }
                
                

                if (isRowEmpty(row)) {
                    continue;
                }
                
                if (row.getLastCellNum() < 7) {
                    System.err.println("Insufficient columns in row: " + row.getRowNum());
                    continue;
                }
                
                try {
                    String subject = getCellValueAsString(row.getCell(0));
                    String courseNo = getCellValueAsString(row.getCell(1));
                    String section = getCellValueAsString(row.getCell(2));
                    String title = getCellValueAsString(row.getCell(3));
                    String faculty = getCellValueAsString(row.getCell(4));
                    
                    int credits = 0;
                    try {
                        String creditsStr = getCellValueAsString(row.getCell(5)).trim();
                        if (!creditsStr.isEmpty()) {
                            credits = Integer.parseInt(creditsStr);
                        }
                    } catch (NumberFormatException ignored) {}
                    
                    String instructor = getCellValueAsString(row.getCell(6));
                    
                    String partOfTerm = row.getLastCellNum() > 7 ? getCellValueAsString(row.getCell(7)) : "";
                    String corequisite = row.getLastCellNum() > 8 ? getCellValueAsString(row.getCell(8)) : "";
                    String prerequisite = row.getLastCellNum() > 9 ? getCellValueAsString(row.getCell(9)) : "";
                    String description = row.getLastCellNum() > 10 ? getCellValueAsString(row.getCell(10)) : "";
                    String schedule = row.getLastCellNum() > 11 ? getCellValueAsString(row.getCell(11)) : "";
                    
                    Course course = new Course(subject, courseNo, section, title, faculty,
                            credits, instructor, partOfTerm,
                            corequisite, prerequisite, description,
                            schedule);
                    
                    courses.add(course);
                    
                } catch (Exception e) {
                    System.err.println("Error processing row " + row.getRowNum() + " in file " + file.getName() + ": " + e.getMessage());
                }
            }
            
            workbook.close();
            
        } catch (IOException e) {
            System.err.println("Error reading file " + file.getName() + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return courses;
    }

    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim();
                } catch (IllegalStateException e) {
                    try {
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue == Math.floor(numericValue)) {
                            return String.valueOf((long) numericValue);
                        } else {
                            return String.valueOf(numericValue);
                        }
                    } catch (IllegalStateException e2) {
                        return "";
                    }
                }
            case BLANK:
            default:
                return "";
        }
    }
    

    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && 
                !getCellValueAsString(cell).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        readCourses("src/assets");
    }
}