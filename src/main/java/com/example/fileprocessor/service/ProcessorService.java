package com.example.fileprocessor.service;

import com.example.fileprocessor.constant.FileProcessStatus;
import com.example.fileprocessor.dto.FileStatusDTO;
import com.example.fileprocessor.model.FileData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProcessorService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final ExcelFileValidator excelFileValidatorService;

    private final ObjectMapper objectMapper;
    @KafkaListener(topics = "upload-topic", groupId = "file-uploader-id")
    public void getFile(String message) throws JsonProcessingException {
        fileContentValidation(objectMapper.readValue(message, FileData.class));
    }
    @SneakyThrows
    private void fileContentValidation(FileData fileData){
        int[] result = new int[2];
        switch (getFileExtensionName(fileData.getFileName())){
            case ".xls": {
                result = excelFileValidatorService.validation(fileData.getFileBytes());
            }
            case ".xlsx":{
                result = excelFileValidatorService.validation(fileData.getFileBytes());
            }
        }

        if (result.length == 2 && result[0] == 2 && result[1] == 6){
            kafkaTemplate.send("status-topic",
                    new FileStatusDTO(fileData.getFileName(), FileProcessStatus.SECOND_VALIDATION_COMPLETED));
        }else {
            kafkaTemplate.send("status-topic",
                    new FileStatusDTO(fileData.getFileName(), FileProcessStatus.SECOND_VALIDATION_FAILED));
        }
    }

    private String getFileExtensionName(String filename){
        int start = filename.lastIndexOf('.');
        return filename.substring(start);
    }

}
