package com.example.fileprocessor.dto;

import com.example.fileprocessor.constant.FileProcessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileStatusDTO {
    private String fileName;
    private FileProcessStatus fileStatus;
}
