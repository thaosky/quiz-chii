package com.quizchii.repository;

import com.quizchii.entity.TestQuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestQuestionRepository extends JpaRepository<TestQuestionEntity, Long> {
    List<TestQuestionEntity> findAllByTestId(Long testId);

}
