package com.quizchii.model.response;


import com.quizchii.Enum.TestType;
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
public class TestResponse {
    private Long id;

    private String name;
    private String description;
    private Integer availableTime;

    private TestType testType;
    private String startTime;
    private String endTime;

    private List<QuestionEntity> questionList; // Trả về cho user thì field câu trả lời đúng phải set null
    private List<TagEntity> tagList;

    private Integer totalSubmit;
    private Integer totalPoint;
}
