package com.quizchii.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AchievementConfigRequest {
    private String name;
    private String message; // Chúc mừng bạn....
    private String daysStreak;
}
