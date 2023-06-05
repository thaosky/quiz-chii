package com.quizchii.repository;

import com.quizchii.entity.ResultDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultDetailRepository extends JpaRepository<ResultDetailEntity, Long> {
}
