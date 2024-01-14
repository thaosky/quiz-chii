package com.quizchii.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAchievementResponse {
    private String name;

    private String message;

    private String timeAchieved;
}
