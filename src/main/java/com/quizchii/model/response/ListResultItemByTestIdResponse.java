package com.quizchii.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListResultItemByTestIdResponse {
    private String username;
    private Long userId;
    private Integer totalQuestion;
    private String startedAt;
    private String submittedAt;
    private Integer corrected;
}
