package com.quizchii.controller;

import com.quizchii.model.ResponseData;
import com.quizchii.model.request.ResultRequest;
import com.quizchii.service.ResultService;
import lombok.AllArgsConstructor;
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
}
