package com.quizchii.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultRequest {
    private Long userId;  // Để lưu lịch sử thi cho user
    private Long testId; // Để lấy danh sách câu hỏi, đáp án đúng
    private String testName;
    private Date startedAt;
    private Date submitAt;
    List<ResultDetailRequest> resultDetails; // Danh sách câu trả lời của user
}
