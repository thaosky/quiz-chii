package com.quizchii.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.quizchii.entity.QuestionEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;


public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String SHEET = "Sheet1";

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
                            question.setCorrectAnswer((int) currentCell.getNumericCellValue());
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
}