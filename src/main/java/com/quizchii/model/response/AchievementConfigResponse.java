package com.quizchii.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementConfigResponse {
    private Long id;
    private String name;
    private String message;
    private Integer daysStreak;
    private String urlImage;
}
