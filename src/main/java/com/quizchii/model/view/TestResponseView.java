package com.quizchii.model.view;

import com.quizchii.Enum.TestType;

import java.sql.Timestamp;

public interface TestResponseView {
    String getName();

    Long getId();

    String getDescription();

    Float getAveragePoint();

    Integer getTotalSubmit();

    Integer getAvailableTime();

    TestType getTestType();

    Timestamp getStartTime();

    Timestamp getEndTime();
}
