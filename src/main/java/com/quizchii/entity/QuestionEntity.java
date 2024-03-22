package com.quizchii.entity;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question")
public class QuestionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String question;

    @Column(length = 1000)
    private String content;

    @Column(length = 5000)
    private String explanation;

    @Column(name = "answer_1")
    private String answer1;
    @Column(name = "answer_2")
    private String answer2;
    @Column(name = "answer_3")
    private String answer3;
    @Column(name = "answer_4")
    private String answer4;

    private Integer correctAnswer;
}
