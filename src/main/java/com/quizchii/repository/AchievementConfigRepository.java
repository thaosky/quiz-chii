package com.quizchii.repository;

import com.quizchii.entity.AchievementConfigEntity;
import com.quizchii.entity.UserAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementConfigRepository extends JpaRepository<AchievementConfigEntity, Long> {
    Optional<AchievementConfigEntity> findByDaysStreak(int daysStreakConfig);
}
