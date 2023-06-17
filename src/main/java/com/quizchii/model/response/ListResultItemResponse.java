package com.quizchii.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListResultItemResponse {
    private Date startedAt;
    private Date submitAt;
    private Integer corrected;
    private String testName;
}
