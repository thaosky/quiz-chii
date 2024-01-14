package com.quizchii.repository;

import com.quizchii.entity.UserAchievementEntity;
import com.quizchii.model.response.UserAchievementResponse;
import com.quizchii.model.view.UserAchievementView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievementEntity, Long> {
    Page<UserAchievementEntity> getAllByUserId(Long userId, Pageable pageable);

    @Query(value = "select ua.time_achieved as timeAchieved, ac.name, ac.message\n" +
            "from user_achievement ua\n" +
            "left join achievement_config ac on ua.achievement_id = ac.id\n" +
            "where ua.user_id = :userId",
            countQuery = "select count(*)\n" +
                    "from user_achievement ua\n" +
                    "left join achievement_config ac on ua.achievement_id = ac.id\n" +
                    "where ua.user_id = :userId",
            nativeQuery = true)
    Page<UserAchievementView> listAchievementByUserId(Long userId, Pageable pageable);
}
