package com.quizchii.controller;

import com.quizchii.model.ResponseData;
import com.quizchii.model.response.TestResponse;
import com.quizchii.service.TestService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/tests")
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
public class TestController {
    private TestService testService;

    @GetMapping
    public ResponseEntity<?> getAllTest(@Param("pageSize") Integer pageSize,
                                        @Param("pageNo") Integer pageNo,
                                        @Param("sortName") String sortName,
                                        @Param("sortDir") String sortDir,
                                        @Param("name") String name,
                                        @Param("tagId") Long tagId
    ) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.getAllTest(pageSize, pageNo, sortName, sortDir, name, tagId)));
    }

    // Lấy bài thi cho Admin (Bao gồm cả câu trả lời)
    @GetMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getByIdAdmin(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.getByIdAdmin(id)));
    }

    // Lấy bài thi cho User (Không bao gồm câu trả lời)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getById(@PathVariable Long id, @Param("userId") Long userId) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.getById(id, userId)));
    }

    // Ai cũng view đc
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewTest(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.viewTest(id)));
    }



    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ResponseData responseData = new ResponseData();
        testService.delete(id);
        return new ResponseEntity<>(responseData,HttpStatus.NO_CONTENT);
    }


    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> deleteIds(@RequestBody List<Long> ids) {
        ResponseData responseData = new ResponseData();
        testService.deleteIds(ids);
        return new ResponseEntity<>(responseData,HttpStatus.NO_CONTENT);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> createTest(@RequestBody TestResponse testResponse) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.create(testResponse)));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    public ResponseEntity<?> updateTest(@RequestBody TestResponse testResponse, @PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.update(testResponse, id)));
    }
}
