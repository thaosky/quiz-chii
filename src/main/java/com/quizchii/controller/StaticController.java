package com.quizchii.controller;

import com.quizchii.model.ResponseData;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/statics")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class StaticController {


    /**
     * Api thống kê lịch sử thi của 1 bài quiz
     * 1. Điểm trung bình của bài thi
     * 2. Trung bình thời gian làm bài
     *
     * Chi tiết
     * 1.1: % dưới 5 điểm
     * 1.2: % <=5 và < 7
     * 1.3: % <= 7 và < 8
     * 1.4: % >= 8
     *
     * @param id bài quiz
     * @return
     */
    @GetMapping("/quiz/{id}")
    public ResponseEntity<?> staticQuiz(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(null));
    }

    /**
     * Api thống kê lịch sử thi của 1 user
     * 1. Điểm trung bình
     * 2. Trung bình thời gian làm bài
     *
     * Chi tiết
     *
     *
     * @param id
     * @return
     */
}
