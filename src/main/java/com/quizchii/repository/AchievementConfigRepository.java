package com.quizchii.repository;

import com.quizchii.entity.AchievementConfigEntity;
import com.quizchii.entity.UserAchievementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementConfigRepository extends JpaRepository<AchievementConfigEntity, Long> {
}
