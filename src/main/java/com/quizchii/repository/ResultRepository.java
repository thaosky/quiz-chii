package com.quizchii.repository;

import com.quizchii.entity.ResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<ResultEntity, Long> {
    List<ResultEntity> getAllByAccountId(Long id);
    List<ResultEntity> getAllByTestId(Long id);
}
