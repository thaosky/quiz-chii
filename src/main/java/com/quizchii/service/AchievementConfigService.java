package com.quizchii.service;

import com.quizchii.common.BusinessException;
import com.quizchii.common.MessageCode;
import com.quizchii.common.Util;
import com.quizchii.entity.AchievementConfigEntity;
import com.quizchii.model.ListResponse;
import com.quizchii.model.request.AchievementConfigRequest;
import com.quizchii.model.response.AchievementConfigResponse;
import com.quizchii.repository.AchievementConfigRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementConfigService {
    private final AchievementConfigRepository achievementConfigRepository;
    private final FileService fileService;

    public ListResponse<AchievementConfigResponse> getAllAchievementConfig(String name, Integer pageSize, Integer pageNo, String sortName, String sortDir) {
        ListResponse<AchievementConfigResponse> response = new ListResponse();

        if ("".equals(name)) {
            name = null;
        }
        Pageable pageable = Util.createPageable(pageSize, pageNo, sortName, sortDir);
        Page<AchievementConfigEntity> page = achievementConfigRepository.listAchievementConfig(name, pageable);

        List<AchievementConfigEntity> list = page.toList();
        List<AchievementConfigResponse> achievementConfigResponses = new ArrayList<>();

        // Mapping
        for (AchievementConfigEntity entity : list) {
            AchievementConfigResponse achievementConfigResponse = new AchievementConfigResponse();
            BeanUtils.copyProperties(entity, achievementConfigResponse);
            achievementConfigResponses.add(achievementConfigResponse);
        }

        response.setItems(achievementConfigResponses);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPage((int) page.getTotalPages());
        return response;
    }

    public AchievementConfigEntity create(AchievementConfigEntity achievementConfig) {
        Optional<AchievementConfigEntity> optional = achievementConfigRepository.findByDaysStreak(achievementConfig.getDaysStreak());
        if (optional.isPresent()) {
            throw new BusinessException(HttpStatus.CONFLICT, MessageCode.ACHIEVEMENT_CONFIG_CONFLICT);
        }
        return achievementConfigRepository.save(achievementConfig);
    }

    public AchievementConfigEntity update(AchievementConfigEntity achievementConfig, Long id) {
        AchievementConfigEntity entity = achievementConfigRepository.findById(id).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.ACHIEVEMENT_CONFIG_NOT_EXIST));
        Optional<AchievementConfigEntity> optional = achievementConfigRepository.findByDaysStreak(achievementConfig.getDaysStreak());
        if (optional.isPresent() && !optional.get().getId().equals(id)) {
            throw new BusinessException(HttpStatus.CONFLICT, MessageCode.ACHIEVEMENT_CONFIG_CONFLICT);
        }
        BeanUtils.copyProperties(achievementConfig, entity, "id");
        return achievementConfigRepository.save(entity);
    }

    public void delete(Long id) {
        AchievementConfigEntity achievementConfig = achievementConfigRepository.findById(id).orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.ACHIEVEMENT_CONFIG_NOT_EXIST));
        achievementConfigRepository.delete(achievementConfig);
    }

    public boolean create(AchievementConfigRequest achievementConfig, MultipartFile image) throws IOException {
        String path = fileService.uploadImage(image);

//        Optional<AchievementConfigEntity> optional = achievementConfigRepository.findByDaysStreak(achievementConfig.getDaysStreak());
//        if (optional.isPresent()) {
//            throw new BusinessException(HttpStatus.CONFLICT, MessageCode.ACHIEVEMENT_CONFIG_CONFLICT);
//        }
//        achievementConfig.setUrlImage(path);
//        achievementConfigRepository.save(achievementConfig);
        return true;
    }
}
