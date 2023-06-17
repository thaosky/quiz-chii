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
public class ListResultItemByTestIdResponse {
    private String username;
    private Long userId;
    private Date startedAt;
    private Date submitAt;
    private Integer corrected;
}
