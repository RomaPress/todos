package com.roman.pres.todos.repository;

import com.roman.pres.todos.model.dao.TodoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoTaskRepository extends JpaRepository<TodoTask, Long> {
}
