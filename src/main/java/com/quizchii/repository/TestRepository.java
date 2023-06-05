package com.quizchii.repository;

import com.quizchii.entity.TestEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<TestEntity, Long> {
    @Query(nativeQuery = true,
            value = "select distinct q.* from test q\n" +
                    "                  left join test_tag qt on qt.test_id = q.id\n" +
                    "                  left join tag t on t.id = qt.tag_id\n" +
                    "where (q.name LIKE  CONCAT('%', :name, '%') or :name is null)\n" +
                    "    and (tag_id =  :tagId or :tagId is null)",
            countQuery = "select count( distinct q.id) from test q\n" +
                    "                  left join test_tag qt on qt.test_id = q.id\n" +
                    "                  left join tag t on t.id = qt.tag_id\n" +
                    "where (q.name LIKE  CONCAT('%', :name, '%') or :name is null)\n" +
                    "    and (tag_id =  :tagId or :tagId is null)"
    )
    Page<TestEntity> listTest(String name, Long tagId, Pageable pageable);
}
