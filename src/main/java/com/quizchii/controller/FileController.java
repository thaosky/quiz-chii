package com.quizchii.controller;

import com.quizchii.common.ExcelHelper;
import com.quizchii.common.MessageCode;
import com.quizchii.model.ResponseData;
import com.quizchii.service.UploadService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("api/file")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class FileController {

    final UploadService uploadService;

    @PostMapping("/upload-excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                uploadService.save(file);

                message = MessageCode.UPLOAD_SUCCESSFULLY + file.getOriginalFilename();
                return ResponseEntity.ok().body(new ResponseData<>().success(message));
            } catch (Exception e) {
                message = MessageCode.CANNOT_UPLOAD + file.getOriginalFilename() + "!";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseData<>().success(message));
            }
        }

        message = MessageCode.NOT_EXCEL_FILE;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseData<>().success(message));
    }

    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    @PostMapping("upload-image")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile image) throws IOException {
        StringBuilder fileName = new StringBuilder();
        Path path = Paths.get(UPLOAD_DIRECTORY, image.getOriginalFilename());
        fileName.append(image.getOriginalFilename());
        Files.write(path, image.getBytes());
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseData<>().success("Upload thành công"));
    }
}