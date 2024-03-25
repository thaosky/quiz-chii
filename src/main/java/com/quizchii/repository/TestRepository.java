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
            value = "select q.id, q.name, q.test_type as testType, q.available_time as availableTime" +
                    ", q.start_time as startTime, q.end_time as endTime, q.description" +
                    ", count(distinct r.id) as totalSubmit, ROUND(SUM(r.corrected / r.total_question *100)/ count(r.id), 2) as averagePoint " +
                    "from test q\n" +
                    "                  left join test_tag qt on qt.test_id = q.id\n" +
                    "                  left join tag t on t.id = qt.tag_id\n" +
                    "left join result r on r.test_id = q.id\n" +
                    "where (q.name LIKE  CONCAT('%', :name, '%') or :name is null)\n" +
                    "    and (tag_id =  :tagId or :tagId is null)" +
                    "    and (test_type =  :testType or :testType is null)\n" +
                    "group by q.id",
            countQuery = "select count(q.id) from test q\n" +
                    "                  left join test_tag qt on qt.test_id = q.id\n" +
                    "                  left join tag t on t.id = qt.tag_id\n" +
                    "left join result r on r.test_id = q.id\n" +
                    "where (q.name LIKE  CONCAT('%', :name, '%') or :name is null)\n" +
                    "    and (tag_id =  :tagId or :tagId is null)\n" +
                    "group by q.id"
    )
    Page<TestResponseView> listTest(String name, Long tagId, String testType, Pageable pageable);
}
