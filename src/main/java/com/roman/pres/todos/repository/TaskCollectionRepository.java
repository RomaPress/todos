package com.roman.pres.todos.repository;

import com.roman.pres.todos.model.dao.TaskCollection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskCollectionRepository extends JpaRepository<TaskCollection, Long> {
    void deleteAllByTodoId(Long todoId);
}
