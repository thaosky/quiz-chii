package com.quizchii.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticDTO {
    private String username;
    private String submittedAt;
    private String timeToTest;
    private String correctResult;
    private Double point;

}
