package com.example.fileprocessor.service;

import com.example.fileprocessor.constant.FileProcessStatus;
import com.example.fileprocessor.dto.FileStatusDTO;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.Iterator;

@Component
public class ExcelFileValidator implements FileValidator {
    @SneakyThrows
    public int[] validation(byte[] bytes) {
        Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(bytes));
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row> rowIterator = sheet.iterator();
        int rowCount = 0;
        int cellCount = 0;
        while (rowIterator.hasNext()){
            Row row = rowIterator.next();
            Iterator<Cell> cellIterator = row.cellIterator();
            while (cellIterator.hasNext()){
                Cell cell = cellIterator.next();
                cellCount++;
                if (cellCount == 3 || cellCount ==6){
                    break;
                }
            }
            rowCount++;
            if (rowCount == 2){
                break;
            }
        }
        return new int[]{rowCount,cellCount};
    }
}
