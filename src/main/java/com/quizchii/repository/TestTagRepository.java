package com.quizchii.repository;

import com.quizchii.entity.TestTagEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestTagRepository extends JpaRepository<TestTagEntity, Long> {
    List<TestTagEntity> findAllByTestId(Long testId);

    List<TestTagEntity> findAllByTagId(Long tagId);
}
