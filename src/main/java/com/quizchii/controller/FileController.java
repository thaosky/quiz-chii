package com.quizchii.controller;

import com.quizchii.common.ExcelHelper;
import com.quizchii.common.MessageCode;
import com.quizchii.model.ResponseData;
import com.quizchii.service.FileService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/file")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class FileController {

    final FileService fileService;

    @PostMapping("/upload-excel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";

        if (ExcelHelper.hasExcelFormat(file)) {
            try {
                fileService.save(file);

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


    @GetMapping("/download-template")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Resource> getFile() {
        String filename = "template.xlsx";
        InputStreamResource file = new InputStreamResource(fileService.downloadTemplate());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}