package com.quizchii.service;

import com.quizchii.Enum.SortDir;
import com.quizchii.common.Util;
import com.quizchii.entity.AchievementConfigEntity;
import com.quizchii.entity.QuestionTagEntity;
import com.quizchii.entity.TagEntity;
import com.quizchii.entity.TestTagEntity;
import com.quizchii.model.ListResponse;
import com.quizchii.model.response.AchievementConfigResponse;
import com.quizchii.model.response.UserAchievementResponse;
import com.quizchii.model.view.UserAchievementView;
import com.quizchii.repository.AchievementConfigRepository;
import com.quizchii.repository.QuestionTagRepository;
import com.quizchii.repository.TagRepository;
import com.quizchii.repository.TestTagRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementConfigService {
    private final AchievementConfigRepository achievementConfigRepository;

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

}
