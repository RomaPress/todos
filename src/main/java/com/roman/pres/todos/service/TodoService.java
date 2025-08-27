package com.roman.pres.todos.service;

import com.roman.pres.todos.exception.TodoPersistenceException;
import com.roman.pres.todos.model.dao.TaskCollection;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dao.TodoTask;
import com.roman.pres.todos.repository.TaskCollectionRepository;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.repository.TodoTaskRepository;
import jakarta.persistence.PersistenceException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final TaskCollectionRepository taskCollectionRepository;
    private final TodoTaskRepository todoTaskRepository;

    @Transactional(rollbackOn = TodoPersistenceException.class)
    public Todo saveTodoWithTask(Todo todo, List<TodoTask> todoTasks) throws TodoPersistenceException {
        try {
            Todo savedTodo = todoRepository.save(todo);

            List<TodoTask> savedTodoTasks = todoTaskRepository.saveAll(todoTasks);

            List<TaskCollection> savedTaskCollectionList = composeTaskCollections(savedTodo, savedTodoTasks);
            taskCollectionRepository.saveAll(savedTaskCollectionList);

            savedTodo.setTaskCollections(savedTaskCollectionList);
            return savedTodo;
        } catch (DataAccessException | PersistenceException e) {
            log.error("Error while saving Todo id={}, name={}", todo.getId(), todo.getName(), e);
            throw new TodoPersistenceException("The problem has occurred while saving todo object", e);
        }
    }

    @Transactional(rollbackOn = TodoPersistenceException.class)
    public void cleanupTaskFromTodo(Long todoId) throws TodoPersistenceException {
        try {
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new TodoPersistenceException("Todo with id = " + todoId + " not found"));
            List<TaskCollection> taskCollectionListToRemove = todo.getTaskCollections();
            List<TodoTask> todoTasksToRemove = taskCollectionListToRemove.stream()
                    .map(TaskCollection::getTodoTask)
                    .toList();
            taskCollectionRepository.deleteAllByTodoId(todo.getId());
            todo.getTaskCollections().clear();
            todoTaskRepository.deleteAll(todoTasksToRemove);
        } catch (DataAccessException | PersistenceException e) {
            log.error("Error while deleting task from Todo id={}", todoId, e);
            throw new TodoPersistenceException("The problem has occurred while deleting task from todo", e);
        }
    }

    @Transactional(rollbackOn = TodoPersistenceException.class)
    public Todo overwriteTodoWithTask(Todo updatedTodo, List<TodoTask> newTask) throws TodoPersistenceException {
        try {
            cleanupTaskFromTodo(updatedTodo.getId());
            return saveTodoWithTask(updatedTodo, newTask);
        } catch (TodoPersistenceException e) {
            throw new TodoPersistenceException(e);
        }
    }

    @Transactional(rollbackOn = TodoPersistenceException.class)
    public Todo cleanupTodoWithTask(Long todoId) throws TodoPersistenceException {
        try {
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new TodoPersistenceException("Todo with id = " + todoId + " not found"));
            List<TaskCollection> taskCollectionListToRemove = todo.getTaskCollections();
            List<TodoTask> todoTasksToRemove = taskCollectionListToRemove.stream()
                    .map(TaskCollection::getTodoTask)
                    .toList();
            taskCollectionRepository.deleteAllByTodoId(todo.getId());
            todo.getTaskCollections().clear();
            todoTaskRepository.deleteAll(todoTasksToRemove);
            todoRepository.delete(todo);
            return todo;
        } catch (DataAccessException | PersistenceException e) {
            log.error("Error while deleting Todo with id={}", todoId, e);
            throw new TodoPersistenceException("The problem has occurred while deleting Todo", e);
        }
    }

    public boolean isTodoExist(Long todoId) {
        if (Objects.nonNull(todoId)) {
            return todoRepository.existsById(todoId);
        }
        return false;
    }

    private List<TaskCollection> composeTaskCollections(Todo todo, List<TodoTask> todoTasks) {
        return todoTasks.stream()
                .map(savedTask -> new TaskCollection(todo, savedTask))
                .collect(Collectors.toList());
    }
}
