package com.quizchii.controller;

import com.quizchii.model.ResponseData;
import com.quizchii.service.AchievementConfigService;
import com.quizchii.service.AchievementService;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/achievements")
public class AchievementController {

    private final AchievementService achievementService;
    private final AchievementConfigService achievementConfigService;

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or  hasRole('USER')")
    public ResponseEntity<?> getAll(@PathVariable("userId") Long userId,
                                     @Param("pageSize") Integer pageSize,
                                     @Param("pageNo") Integer pageNo,
                                     @Param("sortName") String sortName,
                                     @Param("sortDir") String sortDir
    ) {
        return ResponseEntity.ok().body(
                new ResponseData<>()
                        .success(achievementService.getAchievementByUserId(userId, pageSize, pageNo, sortName, sortDir)));
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
                        .success(achievementConfigService.getAllAchievementConfig(name,pageSize, pageNo, sortName, sortDir)));
    }
}
