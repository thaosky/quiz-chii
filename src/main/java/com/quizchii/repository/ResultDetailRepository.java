package com.quizchii.repository;

import com.quizchii.entity.ResultDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultDetailRepository extends JpaRepository<ResultDetailEntity, Long> {
    List<ResultDetailEntity> findAllByResultId(Long id);
}
