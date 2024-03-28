package com.quizchii.repository;

import com.quizchii.entity.TestEntity;
import com.quizchii.model.view.TestResponseView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    @Query(nativeQuery = true,
            value = "select q.id\n" +
                    "     , q.name\n" +
                    "     , q.test_type                                                       as testType\n" +
                    "     , q.available_time                                                  as availableTime\n" +
                    "     , q.start_time                                                      as startTime\n" +
                    "     , q.end_time                                                        as endTime\n" +
                    "     , q.description\n" +
                    "     , count(distinct r.id)                                              as totalSubmit\n" +
                    "     , ROUND(SUM(r.corrected / r.total_question * 100) / count(r.id), 2) as averagePoint\n" +
                    "from test q\n" +
                    "         left join result r on r.test_id = q.id\n" +
                    "\n" +
                    "         left join test_question tq on q.id = tq.test_id\n" +
                    "         left join question_tag qt on qt.question_id = tq.question_id\n" +
                    "         left join tag t2 on qt.tag_id = t2.id\n" +
                    "\n" +
                    "where (q.name LIKE CONCAT('%', :name, '%') or :name is null)\n" +
                    "  and (qt.tag_id = :tagId or :tagId is null)\n" +
                    "  and (q.test_type = :testType or :testType is null)" +
                    "group by q.id",
            countQuery = "select count(q.id) \n from test q\n" +
                    "         left join result r on r.test_id = q.id\n" +
                    "\n" +
                    "         left join test_question tq on q.id = tq.test_id\n" +
                    "         left join question_tag qt on qt.question_id = tq.question_id\n" +
                    "         left join tag t2 on qt.tag_id = t2.id\n" +
                    "\n" +
                    "where (q.name LIKE CONCAT('%', :name, '%') or :name is null)\n" +
                    "  and (qt.tag_id = :tagId or :tagId is null)\n" +
                    "  and (q.test_type = :testType or :testType is null)" +
                    "group by q.id"
    )
    Page<TestResponseView> listTest(String name, Long tagId, String testType, Pageable pageable);
}
