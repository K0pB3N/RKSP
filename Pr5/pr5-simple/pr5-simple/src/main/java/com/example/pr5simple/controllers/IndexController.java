package com.example.pr5simple.controllers;

import com.example.pr5simple.dto.FileInfoDTO;
import com.example.pr5simple.dto.FileUploadDTO;
import com.example.pr5simple.services.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class IndexController {
    @Value("${app.name}")
    private String appName;

    private final FileService fileService;

    @GetMapping("/")
    public String index(Model model) {
        List<FileInfoDTO> fileInfoDTOList = fileService.getSentFiles();

        model.addAttribute("files", fileInfoDTOList);
        model.addAttribute("appName", appName);

        return "index";
    }

    @PostMapping("/upload")
    public String upload(@ModelAttribute FileUploadDTO fileUploadDTO) {
        fileService.save(fileUploadDTO);
        return "redirect:/";
    }

}
