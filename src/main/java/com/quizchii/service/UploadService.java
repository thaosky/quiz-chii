package com.quizchii.service;

import com.quizchii.common.ExcelHelper;
import com.quizchii.entity.QuestionEntity;
import com.quizchii.repository.QuestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class UploadService {
    final QuestionRepository questionRepository;

    public UploadService(QuestionRepository questionRepository) {
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

}
