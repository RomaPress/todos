package com.roman.pres.todos.model.dao;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@EqualsAndHashCode(exclude = {"todo", "todoTask"})
@ToString(exclude = {"todo", "todoTask"})
@NoArgsConstructor
@Entity
@Table(name = "task_collection")
public class TaskCollection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todo_task_id", nullable = false)
    private TodoTask todoTask;

    public TaskCollection(Todo todo, TodoTask todoTask) {
        this.todo = todo;
        this.todoTask = todoTask;
    }
}

