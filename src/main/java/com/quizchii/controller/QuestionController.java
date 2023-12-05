package com.quizchii.controller;

import com.quizchii.entity.TagEntity;
import com.quizchii.model.request.QuestionRequest;
import com.quizchii.model.ResponseData;
import com.quizchii.service.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/questions")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class QuestionController {
    private QuestionService questionService;

    @GetMapping
    public ResponseEntity<?> getAll(@Param("pageSize") Integer pageSize,
                                    @Param("pageNo") Integer pageNo,
                                    @Param("sortName") String sortName,
                                    @Param("sortDir") String sortDir,
                                    @Param("content") String content,
                                    @Param("tagId") Long tagId) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(questionService.getAll(pageSize, pageNo, sortName, sortDir, content, tagId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> create(@RequestBody QuestionRequest request) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(questionService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> create(@RequestBody QuestionRequest request, @PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(questionService.update(request, id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(questionService.getQuestionById(id)));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ResponseData responseData = new ResponseData();
        questionService.delete(id);
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(responseData));
    }

    @PostMapping("/{id}/add-tag")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> addTag(@PathVariable Long id, @RequestBody List<TagEntity> tagList) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(questionService.addTagByQuestion(id, tagList)));
    }
    @DeleteMapping("/{id}/delete-tag/{tagId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> deleteTagByQuestion(@PathVariable Long id, @PathVariable Long tagId) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(questionService.deleteTagByQuestion(id, tagId)));
    }
}
