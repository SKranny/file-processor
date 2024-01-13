package com.example.fileprocessor.service;

import com.example.fileprocessor.model.FileData;

public interface FileValidator {
    int[] validation(byte[] bytes);
}
