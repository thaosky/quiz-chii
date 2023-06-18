package com.quizchii.controller;

import com.quizchii.model.ResponseData;
import com.quizchii.model.request.ResultRequest;
import com.quizchii.service.ResultService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/results")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class ResultController {

   private final ResultService resultService;

    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> submitTest(@RequestBody ResultRequest result) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(resultService.submitTest(result)));
    }

    // Api xem danh sách lịch sử thi của user
    @GetMapping("/user/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> listResultByUserId(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(resultService.listResultByUserId(id)));
    }

    // Api thống kê lịch sử thi của 1 bài quiz
    @GetMapping("/test/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listResultByTestId(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(resultService.listResultByTestId(id)));
    }


     // Api xem chi tiết lịch sử 1 lần thi
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> getResultDetail(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(resultService.getResultDetail(id)));
    }
}
