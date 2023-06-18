package com.quizchii.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResultDetailResponse {
    private String content;

    private String question;

    private String answer1;

    private String answer2;

    private String answer3;

    private String answer4;

    private Integer correctAnswer;
    private Integer answered;
}
