package org.example.repository;

import org.example.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    Page<Todo> findByUserId(Long userId, Pageable pageable);
    Optional<Todo> findByIdAndUserId(Long id, Long userId);
}
