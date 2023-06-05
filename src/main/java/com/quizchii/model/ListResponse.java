package com.quizchii.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListResponse<T> {
    private Integer totalPage;
    private Integer totalElements;
    private Integer pageSize;
    private Integer pageNo;
    private List<T> items;
}
