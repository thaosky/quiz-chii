package com.quizchii.repository;

import com.quizchii.entity.QuestionTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTagEntity, Long> {
    Optional<QuestionTagEntity> findByQuestionIdAndTagId(Long questionId, Long tagId);
    List<QuestionTagEntity> findAllByQuestionId(Long questionId);
    List<QuestionTagEntity> findAllByTagId(Long tagId);
}
