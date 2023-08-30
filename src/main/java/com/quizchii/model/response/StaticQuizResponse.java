package com.quizchii.model.response;

import com.quizchii.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StaticQuizResponse {
    private Long quizId;
    private String quizName;

//    private List<UserEntity> userList; // Danh sách user tham gia
    private Integer numberOfUserJoin;
    private Integer numberOfTakeQuiz;
    private Float avgPoint;
    private Float avgTime;
    private Float percentLess5;
    private Float percent5ToLess7;
    private Float percent7ToLess8;
    private Float percentFrom8;
}
