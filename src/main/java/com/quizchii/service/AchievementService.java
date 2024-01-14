package com.quizchii.service;

import com.quizchii.Enum.SortDir;
import com.quizchii.common.BusinessException;
import com.quizchii.common.MessageCode;
import com.quizchii.common.Util;
import com.quizchii.entity.UserEntity;
import com.quizchii.model.ListResponse;
import com.quizchii.model.response.UserAchievementResponse;
import com.quizchii.model.view.UserAchievementView;
import com.quizchii.repository.AchievementConfigRepository;
import com.quizchii.repository.UserAchievementRepository;
import com.quizchii.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
public class AchievementService {

    private final AchievementConfigRepository achievementConfigRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final UserRepository userRepository;

    public ListResponse<UserAchievementResponse> getAchievementByUserId(Long userId, Integer pageSize, Integer pageNo, String sortName, String sortDir) {
        ListResponse<UserAchievementResponse> response = new ListResponse();

        create(userId);

        // Paging
        Sort sortable = Sort.by("id").descending();
        if (sortName != null && SortDir.ASC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).ascending();
        } else if (sortName != null && SortDir.DESC.getValue().equals(sortDir)) {
            sortable = Sort.by(sortName).descending();
        }
        Pageable pageable = PageRequest.of(pageNo, pageSize, sortable);

        Page<UserAchievementView> page = userAchievementRepository.listAchievementByUserId(userId, pageable);

        List<UserAchievementView> list = page.toList();
        List<UserAchievementResponse> questionResponseList = new ArrayList<>();

        // Mapping
        for (UserAchievementView view : list) {
            UserAchievementResponse userAchievementResponse = new UserAchievementResponse();
            BeanUtils.copyProperties(view, userAchievementResponse);
            questionResponseList.add(userAchievementResponse);
        }

        response.setItems(questionResponseList);
        response.setPageNo(pageNo);
        response.setPageSize(pageSize);
        response.setTotalElements((int) page.getTotalElements());
        response.setTotalPage((int) page.getTotalPages());
        return response;
    }

    // Xem da hoc bao nhieu ngay lien tiep
    public int create(Long userId) {
        Optional<UserEntity> optional = userRepository.findById(userId);
        UserEntity userEntity = optional.orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, MessageCode.USER_NOT_EXIST));

        // Neu user chua hoc ngay nao => Set streak = 1
        if (userEntity.getLastActive() == null) {
            return 1;
        }
        long time1 = userEntity.getLastActive().getTime(); // (Mon 31 December 2018 13:19:25)
        long time2 = new Timestamp(System.currentTimeMillis()).getTime();

        int res = Util.daysBetween(time1, time2);
        return res + 1;
    }


}
