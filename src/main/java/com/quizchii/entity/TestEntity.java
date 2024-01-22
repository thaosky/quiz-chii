package com.quizchii.entity;

import javax.persistence.*;

import com.quizchii.Enum.TestType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "test")
public class TestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private Integer availableTime;

    // test mode = ONCE_WITH_TIME => start time, end time not null
    @Enumerated(EnumType.STRING)
    private TestType testType;
    private Timestamp startTime;
    private Timestamp endTime;
}
