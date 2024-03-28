package com.quizchii.repository;

import com.quizchii.entity.TagEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<TagEntity, Long> {

    @Query(value = "select * from tag\n" +
            "where tag.name = :name or :name is null",
            countQuery = "select count(*) from tag\n" +
                    "where tag.name = :name or :name is null"
            , nativeQuery = true)
    Page<TagEntity> findAllByName(String name, Pageable pageable);


    @Query(value = "select distinct t.*\n" +
            "from question_tag qt\n" +
            "         left join tag t on t.id = qt.tag_id\n" +
            "where qt.question_id = :questionId\n" +
            "order by t.id",
            nativeQuery = true)
    List<TagEntity> findAllByQuestionId(Long questionId);

    @Query(value = "select distinct t2.*\n" +
            "from test t\n" +
            "         left join test_question tq on t.id = tq.test_id\n" +
            "         left join question_tag qt on qt.question_id = tq.question_id\n" +
            "         left join tag t2 on qt.tag_id = t2.id\n" +
            "where t.id = :testId\n" +
            "order by t2.id",
            nativeQuery = true)
    List<TagEntity> findAllByTestId(Long testId);
}
