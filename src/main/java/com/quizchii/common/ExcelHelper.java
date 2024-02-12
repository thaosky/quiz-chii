package com.quizchii.common;

import com.quizchii.entity.QuestionEntity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Sheet1";
    static String[] HEADERS = {"Câu hỏi",
            "Nội dung",
            "Phương án A",
            "Phương án B",
            "Phương án C",
            "Phương án D",
            "Phương án đúng",
            "Giải thích"
    };

    static Map<String, Integer> optionMap = new HashMap<String, Integer>() {
        {
            put("A", 1);
            put("B", 2);
            put("C", 3);
            put("D", 4);
        }
    };

    static String[] DROPDOWN_OPTION = {"A", "B", "C", "D"};
    static int[] widthColumns = {4, 3, 3, 3, 3, 3, 3, 3};

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    public static List<QuestionEntity> excelToQuestions(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);

            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();

            List<QuestionEntity> questionList = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();

                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                QuestionEntity question = new QuestionEntity();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();

                    switch (cellIdx) {
                        case 0:
                            question.setQuestion(currentCell.getStringCellValue());
                            break;
                        case 1:
                            question.setContent(currentCell.getStringCellValue());
                            break;
                        case 2:
                            question.setAnswer1(currentCell.getStringCellValue());
                            break;
                        case 3:
                            question.setAnswer2(currentCell.getStringCellValue());
                            break;
                        case 4:
                            question.setAnswer3(currentCell.getStringCellValue());
                            break;
                        case 5:
                            question.setAnswer4(currentCell.getStringCellValue());
                            break;
                        case 6:
                            String correctAnswer = currentCell.getStringCellValue();
                            Integer correctInt = optionMap.get(correctAnswer);
                            question.setCorrectAnswer(correctInt);
                            break;
                        case 7:
                            question.setExplanation(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }

                    cellIdx++;
                }

                questionList.add(question);
            }

            workbook.close();

            return questionList;
        } catch (IOException e) {
            throw new RuntimeException("Fail to parse Excel file: " + e.getMessage());
        }
    }

    public static ByteArrayInputStream createTemplate() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            XSSFSheet sheet = (XSSFSheet) workbook.createSheet(SHEET);

            // Dropdown
            DataValidation dataValidation = null;
            DataValidationConstraint constraint = null;
            DataValidationHelper validationHelper = null;

            validationHelper = new XSSFDataValidationHelper(sheet);
            CellRangeAddressList addressList = new CellRangeAddressList(1, 1, 6, 6);
            constraint = validationHelper.createExplicitListConstraint(DROPDOWN_OPTION);
            dataValidation = validationHelper.createValidation(constraint, addressList);
            dataValidation.setSuppressDropDownArrow(true);
            sheet.addValidationData(dataValidation);
            // End Dropdown

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
                CellStyle cellStyle = workbook.createCellStyle();
                Font boldFont = workbook.createFont();
                boldFont.setBold(true);
                boldFont.setFontHeightInPoints((short) 13);
                cellStyle.setFont(boldFont);
                cellStyle.setAlignment(HorizontalAlignment.CENTER);
                cell.setCellStyle(cellStyle);
                sheet.setColumnWidth(col, widthColumns[col] * 1500);
            }

            int rowIdx = 1;
            // Tạo mock data
            for (int i = 0; i < 1; i++) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue("Chọn phương án đúng");
                row.createCell(1).setCellValue("How are you?");
                row.createCell(2).setCellValue("I'm fine");
                row.createCell(3).setCellValue("I'm 20 years old");
                row.createCell(4).setCellValue("Me too");
                row.createCell(5).setCellValue("Yes");
                row.createCell(6).setCellValue("A");
                row.createCell(7).setCellValue("A đúng ngữ pháp");
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }
}