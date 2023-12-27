package com.example.fileprocessor.model;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.core.serializer.Deserializer;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class FileData {

    private String fileName;

    private byte[] fileBytes;
}
