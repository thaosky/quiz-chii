package com.quizchii.controller;

import com.quizchii.entity.TagEntity;
import com.quizchii.model.ResponseData;
import com.quizchii.service.TagService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tags")
@CrossOrigin(origins = "*", maxAge = 3600)
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class TagController {

    private TagService tagService;

    @GetMapping
    public ResponseEntity<?> getAllTag(@Param("pageSize") Integer pageSize,
                                       @Param("pageNo") Integer pageNo,
                                       @Param("sortName") String sortName,
                                       @Param("sortDir") String sortDir,
                                       @Param("name") String name) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(tagService.getAllTag(pageSize, pageNo, sortName, sortDir, name)));
    }

    @PostMapping
    public ResponseEntity<?> createTag(@RequestBody TagEntity tag) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(tagService.create(tag)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTag(@RequestBody TagEntity tagEntity, @PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(tagService.update(tagEntity, id)));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTag(@PathVariable Long id) {
        tagService.delete(id);
        return new ResponseEntity<>(new ResponseData<>()
                .success("Delete successfully!"), HttpStatus.NO_CONTENT);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(tagService.getTagById(id)));
    }

}
