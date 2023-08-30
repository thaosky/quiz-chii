package com.quizchii.repository;

import com.quizchii.entity.QuestionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Long> {
    @Query(nativeQuery = true,
            value = "select distinct q.* from question q\n" +
                    "                  left join question_tag qt on qt.question_id = q.id\n" +
                    "                  left join tag t on t.id = qt.tag_id\n" +
                    "where (q.question LIKE  CONCAT('%', :content, '%') or :content is null)\n" +
                    "    and (tag_id =  :tagId or :tagId is null)",
            countQuery = "select count( distinct q.id) from question q\n" +
                    "                  left join question_tag qt on qt.question_id = q.id\n" +
                    "                  left join tag t on t.id = qt.tag_id\n" +
                    "where (q.question LIKE  CONCAT('%', :content, '%') or :content is null)\n" +
                    "    and (tag_id =  :tagId or :tagId is null)"
    )
    Page<QuestionEntity> listQuestion(String content, Long tagId, Pageable pageable);

    @Query(nativeQuery = true,
            value = "select distinct q.* from question q\n" +
                    "                  left join test_question tq on tq.question_id = q.id\n" +
                    "where tq.test_id = :testId"

    )
    List<QuestionEntity> findAllByTestId(Long testId);
}
