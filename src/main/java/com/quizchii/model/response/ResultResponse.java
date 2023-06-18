package com.quizchii.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultResponse {
    private Long userId;
    private String username;
    private String startedAt;
    private String submitAt;
    private Integer corrected; // Số câu tl đúng
    List<ResultDetailResponse> resultDetails; // Danh sách câu hỏi, đáp án, phương án đã chọn
}
