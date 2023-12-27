package com.example.fileprocessor.service;

import com.example.fileprocessor.constant.FileProcessStatus;
import com.example.fileprocessor.dto.FileStatusDTO;
import com.example.fileprocessor.model.FileData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.*;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.io.*;
import java.util.Iterator;

@Service
@RequiredArgsConstructor
public class ProcessorService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ObjectMapper objectMapper;
    @KafkaListener(topics = "upload-topic", groupId = "file-uploader-id")
    public void getFile(String message) throws JsonProcessingException {
        fileContentValidation(objectMapper.readValue(message, FileData.class));
    }
    @SneakyThrows
    private void fileContentValidation(FileData fileData){
        File file = new File("/data/"+fileData.getFileName());

        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            bufferedOutputStream.write(fileData.getFileBytes());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Workbook workbook = WorkbookFactory.create(file);
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
        if (rowCount == 2 && cellCount == 6){
            kafkaTemplate.send("status-topic",
                    new FileStatusDTO(fileData.getFileName(), FileProcessStatus.SECOND_VALIDATION_COMPLETED));
        }else {
            kafkaTemplate.send("status-topic",
                    new FileStatusDTO(fileData.getFileName(), FileProcessStatus.SECOND_VALIDATION_FAILED));
        }

        file.delete();
    }

}
