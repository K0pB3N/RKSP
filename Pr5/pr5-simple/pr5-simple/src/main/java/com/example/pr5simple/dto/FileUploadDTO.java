package com.example.pr5simple.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class FileUploadDTO {
    private List<MultipartFile> files;
}
