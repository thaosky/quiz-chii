package com.quizchii.repository;

import com.quizchii.entity.AchievementConfigEntity;
import com.quizchii.entity.UserAchievementEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementConfigRepository extends JpaRepository<AchievementConfigEntity, Long> {
    Optional<AchievementConfigEntity> findByDaysStreak(int daysStreakConfig);

    @Query(value = "select ac.* from achievement_config ac\n" +
            "where (ac.name LIKE  CONCAT('%', :name, '%') or :name is null)",
            countQuery = "select count(*) from achievement_config ac\n" +
                    "where (ac.name LIKE  CONCAT('%', :name, '%') or :name is null)",
            nativeQuery = true)
    Page<AchievementConfigEntity> listAchievementConfig(String name, Pageable pageable);
}
