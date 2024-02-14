package com.quizchii.service;

import com.quizchii.common.ExcelHelper;
import com.quizchii.entity.QuestionEntity;
import com.quizchii.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileService {
    final QuestionRepository questionRepository;
    public static String UPLOAD_DIRECTORY = System.getProperty("user.dir") + "/uploads";

    public FileService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public void save(MultipartFile file) {
        try {
            List<QuestionEntity> tutorials = ExcelHelper.excelToQuestions(file.getInputStream());
            questionRepository.saveAll(tutorials);
        } catch (IOException e) {
            throw new RuntimeException("Fail to store excel data: " + e.getMessage());
        }
    }

    public String uploadImage(MultipartFile image) throws IOException {
        StringBuilder fileName = new StringBuilder();
        Path path = Paths.get(UPLOAD_DIRECTORY, image.getOriginalFilename());
        fileName.append(image.getOriginalFilename());
        Files.write(path, image.getBytes());

        return path.toString();
    }


    public ByteArrayInputStream downloadTemplate() {
        ByteArrayInputStream in = ExcelHelper.createTemplate();
        return in;
    }
}