package com.quizchii.model.question;


import com.quizchii.entity.QuestionEntity;
import com.quizchii.entity.TagEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TestDTO {
    private Long id;

    private String name;
    private String description;
    private Integer availableTime;

    private List<QuestionEntity> questionList;
    private List<TagEntity> tagList;
}
