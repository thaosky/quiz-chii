package com.quizchii.repository;

import com.quizchii.entity.QuestionEntity;
import com.quizchii.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);
    Boolean existsByUsernameAndActive(String username, Integer active);
    Boolean existsByEmail(String email);

    @Query(nativeQuery = true,
            value = "select u.*\n" +
                    "from users u\n" +
                    "where (u.username LIKE CONCAT('%', :username, '%') or :username is null)\n" +
                    "  and (u.name LIKE CONCAT('%', :name, '%') or :name is null)",
            countQuery = "select count(u.id)\n" +
                    "from users u\n" +
                    "where (u.username LIKE CONCAT('%', :username, '%') or :username is null)\n" +
                    "  and (u.name LIKE CONCAT('%', :name, '%') or :name is null)"
    )
    Page<UserEntity> listUser(String username, String name, Pageable pageable);
}