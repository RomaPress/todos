package com.roman.pres.todos.service;

import com.roman.pres.todos.exception.TodoPersistenceException;
import com.roman.pres.todos.model.dao.TaskCollection;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dao.TodoTask;
import com.roman.pres.todos.repository.TaskCollectionRepository;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.repository.TodoTaskRepository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private TaskCollectionRepository taskCollectionRepository;
    @Mock
    private TodoTaskRepository todoTaskRepository;

    @Spy
    @InjectMocks
    private TodoService todoService;

    @Test
    void saveTodoWithTask_success() throws TodoPersistenceException {
        // given
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setName("test");

        TodoTask task = new TodoTask();
        task.setId(10L);

        when(todoRepository.save(todo)).thenReturn(todo);
        when(todoTaskRepository.saveAll(any())).thenReturn(List.of(task));
        when(taskCollectionRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        Todo result = todoService.saveTodoWithTask(todo, List.of(task));

        // then
        assertEquals(1L, result.getId());
        assertEquals(1, result.getTaskCollections().size());
        verify(todoRepository).save(todo);
        verify(todoTaskRepository).saveAll(any());
        verify(taskCollectionRepository).saveAll(any());
    }

    @Test
    void saveTodoWithTask_throwsException() {
        Todo todo = new Todo();
        when(todoRepository.save(todo)).thenThrow(new PersistenceException("DB error"));

        assertThrows(TodoPersistenceException.class,
                () -> todoService.saveTodoWithTask(todo, List.of(new TodoTask())));
    }

    @Test
    void cleanupTaskFromTodo_success() throws TodoPersistenceException {
        // given
        Todo todo = new Todo();
        todo.setId(2L);

        TodoTask task = new TodoTask();
        TaskCollection collection = new TaskCollection(todo, task);
        List<TaskCollection> taskCollections = new ArrayList<>();
        taskCollections.add(collection);
        todo.setTaskCollections(taskCollections);

        when(todoRepository.findById(2L)).thenReturn(Optional.of(todo));

        // when
        todoService.cleanupTaskFromTodo(2L);

        // then
        verify(taskCollectionRepository).deleteAllByTodoId(2L);
        verify(todoTaskRepository).deleteAll(List.of(task));
        assertTrue(todo.getTaskCollections().isEmpty());
    }

    @Test
    void cleanupTaskFromTodo_notFound() {
        when(todoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(TodoPersistenceException.class,
                () -> todoService.cleanupTaskFromTodo(99L));
    }

    @Test
    void overwriteTodoWithTask_success() throws TodoPersistenceException {
        // given
        Todo todo = new Todo();
        todo.setId(3L);

        TodoTask task = new TodoTask();

        when(todoRepository.findById(3L)).thenReturn(Optional.of(todo));
        when(todoRepository.save(todo)).thenReturn(todo);
        when(todoTaskRepository.saveAll(any())).thenReturn(List.of(task));
        when(taskCollectionRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

        // when
        Todo result = todoService.overwriteTodoWithTask(todo, List.of(task));

        // then
        assertEquals(3L, result.getId());
    }

    @Test
    void cleanupTodoWithTask_success() throws TodoPersistenceException {
        // given
        Todo todo = new Todo();
        todo.setId(5L);

        TodoTask task = new TodoTask();
        TaskCollection collection = new TaskCollection(todo, task);
        List<TaskCollection> taskCollections = new ArrayList<>();
        taskCollections.add(collection);
        todo.setTaskCollections(taskCollections);

        when(todoRepository.findById(5L)).thenReturn(Optional.of(todo));

        // when
        todoService.cleanupTodoWithTask(5L);

        // then
        verify(taskCollectionRepository).deleteAllByTodoId(5L);
        verify(todoTaskRepository).deleteAll(List.of(task));
        verify(todoRepository).delete(todo);
        assertTrue(todo.getTaskCollections().isEmpty());
    }

    @Test
    void isTodoExist_returnsTrue() {
        when(todoRepository.existsById(1L)).thenReturn(true);
        assertTrue(todoService.isTodoExist(1L));
    }

    @Test
    void isTodoExist_nullId_returnsFalse() {
        assertFalse(todoService.isTodoExist(null));
    }

    @Test
    void saveTodoWithTask_throwsTodoPersistenceException() {
        Todo todo = new Todo();
        TodoTask task = new TodoTask();

        when(todoRepository.save(todo)).thenThrow(new PersistenceException("DB error"));

        assertThrows(TodoPersistenceException.class,
                () -> todoService.saveTodoWithTask(todo, List.of(task)));
    }

    @Test
    void cleanupTaskFromTodo_throwsTodoPersistenceException() {
        Todo todo = new Todo();
        todo.setId(1L);
        TodoTask task = new TodoTask();
        TaskCollection collection = new TaskCollection(todo, task);
        todo.setTaskCollections(new java.util.ArrayList<>(List.of(collection)));

        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        doThrow(new PersistenceException("DB error"))
                .when(taskCollectionRepository).deleteAllByTodoId(1L);

        assertThrows(TodoPersistenceException.class,
                () -> todoService.cleanupTaskFromTodo(1L));
    }

    @Test
    void cleanupTodoWithTask_throwsTodoPersistenceException() {
        Todo todo = new Todo();
        todo.setId(2L);
        TodoTask task = new TodoTask();
        TaskCollection collection = new TaskCollection(todo, task);
        todo.setTaskCollections(new java.util.ArrayList<>(List.of(collection)));

        when(todoRepository.findById(2L)).thenReturn(Optional.of(todo));

        doThrow(new PersistenceException("DB error"))
                .when(taskCollectionRepository).deleteAllByTodoId(2L);

        assertThrows(TodoPersistenceException.class,
                () -> todoService.cleanupTodoWithTask(2L));
    }

    @Test
    void overwriteTodoWithTask_throwsTodoPersistenceException() throws TodoPersistenceException {
        Todo todo = new Todo();
        todo.setId(1L);
        TodoTask task = new TodoTask();

        doThrow(new TodoPersistenceException("cleanup error"))
                .when(todoService).cleanupTaskFromTodo(1L);

        TodoPersistenceException ex = assertThrows(TodoPersistenceException.class,
                () -> todoService.overwriteTodoWithTask(todo, List.of(task)));

        assertEquals("cleanup error", ex.getCause().getMessage());
    }
}