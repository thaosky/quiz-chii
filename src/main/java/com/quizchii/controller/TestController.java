package com.quizchii.controller;

import com.quizchii.model.ResponseData;
import com.quizchii.model.question.TestDTO;
import com.quizchii.service.TestService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.getById(id)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        ResponseData responseData = new ResponseData();
        testService.delete(id);
        return new ResponseEntity<>(responseData,HttpStatus.NO_CONTENT);
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createTest(@RequestBody TestDTO testDTO) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.create(testDTO)));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTest(@RequestBody TestDTO testDTO, @PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(testService.update(testDTO, id)));
    }
}
