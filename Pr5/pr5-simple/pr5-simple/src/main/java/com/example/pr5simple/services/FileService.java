package com.example.pr5simple.services;

import com.example.pr5simple.dto.FileInfoDTO;
import com.example.pr5simple.dto.FileUploadDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {
    @Value("${app.path.upload.file}")
    private String uploadPath;

    private SimpleDateFormat formatForDateNow = new SimpleDateFormat("hh:mm dd.MM.yyyy");

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadPath));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload folder!");
        }
   
        formatForDateNow.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public void save(FileUploadDTO fileUploadDTO) {
        for(MultipartFile multipartFile: fileUploadDTO.getFiles()) {
            String newFileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();

            saveFile(multipartFile,newFileName);
        }
    }

    public List<FileInfoDTO> getSentFiles() {
        List<FileInfoDTO> fileInfoDTOList = new ArrayList<>();
        List<File> uploadFileList = getUploadFilesFromFolder();

        for (File file: uploadFileList) {
            FileInfoDTO fileInfoDTO = new FileInfoDTO();

            fileInfoDTO.setName(file.getName());
            fileInfoDTO.setDate(formatForDateNow.format(new Date(file.lastModified())));
            fileInfoDTO.setUrl("/upload-files/" + file.getName());

            fileInfoDTOList.add(fileInfoDTO);
        }

        return fileInfoDTOList;
    }

    private void saveFile(MultipartFile file, String fileName) {
        try {
            Path root = Paths.get(uploadPath);
            if (!Files.exists(root)) {
                init();
            }
            Files.copy(file.getInputStream(), root.resolve(fileName));
        } catch (Exception e) {
            throw new RuntimeException("Could not store the file. Error: " + e.getMessage());
        }
    }

    private List<File> getUploadFilesFromFolder() {
        List<File> files = new ArrayList<>();
        File folder = new File(uploadPath);

        for (final File fileEntry : folder.listFiles()) {
            files.add(fileEntry);
        }

        return files;
    }
}

