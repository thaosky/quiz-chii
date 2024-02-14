package com.quizchii.controller;

import com.quizchii.entity.AchievementConfigEntity;
import com.quizchii.model.ResponseData;
import com.quizchii.model.request.AchievementConfigRequest;
import com.quizchii.service.AchievementConfigService;
import com.quizchii.service.AchievementService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/api/achievements")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AchievementController {

    private final AchievementService achievementService;
    private final AchievementConfigService achievementConfigService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or  hasRole('USER')")
    public ResponseEntity<?> getAll(@Param("pageSize") Integer pageSize,
                                    @Param("pageNo") Integer pageNo,
                                    @Param("sortName") String sortName,
                                    @Param("sortDir") String sortDir
    ) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(achievementService.getAchievementByUser(pageSize, pageNo, sortName, sortDir)));
    }

    @GetMapping("/daily")
    @PreAuthorize("hasRole('ADMIN') or  hasRole('USER')")
    public ResponseEntity<?> getCurrStreak(
    ) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(achievementService.getCurrStreak()));
    }


    @GetMapping("/configs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAchievementConfig(
            @Param("pageSize") Integer pageSize,
            @Param("pageNo") Integer pageNo,
            @Param("sortName") String sortName,
            @Param("sortDir") String sortDir,
            @Param("name") String name
    ) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(achievementConfigService.getAllAchievementConfig(name, pageSize, pageNo, sortName, sortDir)));
    }

    @PostMapping("/configs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAchievementConfig(
            @RequestBody AchievementConfigEntity achievementConfig
    ) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(achievementConfigService.create(achievementConfig)));
    }

    @PostMapping("/configs/2")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAchievementConfig2(
            @RequestParam AchievementConfigRequest achievementConfig,
            @RequestParam("image") MultipartFile image
    ) throws IOException {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(achievementConfigService.create(achievementConfig, image)));
    }

    @PutMapping("/configs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateAchievementConfig(
            @RequestBody AchievementConfigEntity achievementConfig,
            @PathVariable Long id
    ) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(achievementConfigService.update(achievementConfig, id)));
    }


    @DeleteMapping("/configs/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAchievementConfig(
            @PathVariable Long id
    ) {
        achievementConfigService.delete(id);
        return new ResponseEntity<>(new ResponseData<>()
                .success("Delete successfully!"), HttpStatus.NO_CONTENT);
    }

}
