package com.quizchii.service;

import com.quizchii.common.MessageCode;
import com.quizchii.common.Util;
import com.quizchii.entity.AchievementConfigEntity;
import com.quizchii.entity.UserAchievementEntity;
import com.quizchii.entity.UserEntity;
import com.quizchii.model.ListResponse;
import com.quizchii.model.response.DailyStreakResponse;
import com.quizchii.model.response.UserAchievementResponse;
import com.quizchii.model.view.UserAchievementView;
import com.quizchii.repository.AchievementConfigRepository;
import com.quizchii.repository.UserAchievementRepository;
import com.quizchii.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AchievementService {

    private final AchievementConfigRepository achievementConfigRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    public ListResponse<UserAchievementResponse> getAchievementByUser(Integer pageSize, Integer pageNo, String sortName, String sortDir) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        UserEntity userEntity = userRepository.getByUsername(username);

        ListResponse<UserAchievementResponse> response = new ListResponse();
        Pageable pageable = Util.createPageable(pageSize, pageNo, sortName, sortDir);

        Page<UserAchievementView> page = userAchievementRepository.listAchievementByUserId(userEntity.getId(), pageable);

        List<UserAchievementView> list = page.toList();
        List<UserAchievementResponse> questionResponseList = new ArrayList<>();
        UserAchievementResponse achievementDaily = new UserAchievementResponse();
        achievementDaily.setMessage(String.format(MessageCode.ACHIEVEMENT_DAILY, userEntity.getCurrDaysStreak()));
        achievementDaily.setName(MessageCode.ACHIEVEMENT_DAILY_CODE);
        achievementDaily.setTimeAchieved(Util.convertTimestampToString(userEntity.getDateGetMaxStreak()));
        questionResponseList.add(achievementDaily);

        // Mapping
        for (UserAchievementView view : list) {
            UserAchievementResponse userAchievementResponse = new UserAchievementResponse();
            BeanUtils.copyProperties(view, userAchievementResponse);
            userAchievementResponse.setTimeAchieved(Util.convertTimestampToString(view.getTimeAchieved()));
            questionResponseList.add(userAchievementResponse);
        }

        response.setItems(questionResponseList);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements((int) page.getTotalElements() + 1);
        response.setTotalPage((int) page.getTotalPages());
        return response;
    }

    public boolean createAchievement(Long userId, int dayStreak) {
        Optional<AchievementConfigEntity> achievementConfigOptional = achievementConfigRepository.findByDaysStreak(dayStreak);

        if (achievementConfigOptional.isEmpty()) return false;
        AchievementConfigEntity achievementConfig = achievementConfigOptional.get();
        // Chỉ nhận được phần thưởng ở lần đầu
        Optional<UserAchievementEntity> userAchievementEntityOptional = userAchievementRepository
                .getAllByUserIdAndAchievementId(userId, achievementConfig.getId());

        if (userAchievementEntityOptional.isPresent()) return false;

        // Có config suiable + chưa nhận lần nào => Lưu
        UserAchievementEntity userAchievementEntity = new UserAchievementEntity();
        userAchievementEntity.setAchievementId(achievementConfig.getId());
        userAchievementEntity.setUserId(userId);
        userAchievementRepository.save(userAchievementEntity);
        return true;
    }

    public DailyStreakResponse getCurrStreak() {
        // Get curr user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        UserEntity userEntity = userRepository.getByUsername(username);

        // Return
        DailyStreakResponse response = new DailyStreakResponse();
        response.setMessage(String.format(MessageCode.ACHIEVEMENT_DAILY, userEntity.getCurrDaysStreak()));
        response.setDays(userEntity.getCurrDaysStreak());
        return response;
    }
}
