package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.exception.TodoPersistenceException;
import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dto.TaskRequest;
import com.roman.pres.todos.model.dto.TodoRequest;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.service.TodoService;
import com.roman.pres.todos.util.TodoUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateTodoWithTaskCmdTest {
    @Mock
    private TodoService todoService;

    @Mock
    private TodoUtil todoUtil;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private UpdateTodoWithTaskCmd updateTodoWithTaskCmd;

    @Test
    void shouldReturnErrorResponseWhenTodoNotFound() {
        // given
        Long todoId = 1L;
        TodoRequest todoRequest = new TodoRequest("newName", "desc",
                List.of(new TaskRequest("t1", "d1")));
        RequestConfig request = new RequestConfig(Pair.of(RequestKey.TODO_ID, todoId),
                Pair.of(RequestKey.TODO_REQUEST_OBJ, todoRequest));

        when(todoService.isTodoExist(1L)).thenReturn(false);
        when(todoUtil.createErrorResponse("Todo with id = 1 not found", HttpStatus.NOT_FOUND.value()))
                .thenReturn(createError("Todo with id = 1 not found", HttpStatus.NOT_FOUND.value()));

        // when
        TodoResponse result = updateTodoWithTaskCmd.execute(request);

        // then
        assertEquals("Todo with id = 1 not found", result.getErrorMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getErrorCode());

        verify(todoService).isTodoExist(1L);
        verify(todoUtil).createErrorResponse(contains("Todo with id = 1 not found"), eq(404));
    }

    @Test
    void shouldUpdateTodoAndReturnResponse() throws TodoPersistenceException {
        // given
        Long todoId = 1L;

        Todo todo = new Todo();
        todo.setId(todoId);

        TodoRequest todoRequest = new TodoRequest("newName", null,
                List.of(new TaskRequest("t1", "d1")));
        RequestConfig request = new RequestConfig(Pair.of(RequestKey.TODO_ID, todoId),
                Pair.of(RequestKey.TODO_REQUEST_OBJ, todoRequest));

        when(todoService.isTodoExist(todoId)).thenReturn(true);
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoUtil.getDescription(null, "newName", 1)).thenReturn("newName consist of 1 task");

        Todo updatedTodo = new Todo();
        updatedTodo.setId(todoId);
        updatedTodo.setName("newName");
        updatedTodo.setDescription("newName consist of 1 task");

        when(todoService.overwriteTodoWithTask(eq(todo), anyList())).thenReturn(updatedTodo);

        // when
        TodoResponse result = updateTodoWithTaskCmd.execute(request);

        // then
        assertNull(result.getErrorMessage());
        assertEquals(todoId, result.getId());
        assertEquals("newName", result.getName());
        assertEquals("newName consist of 1 task", result.getDescription());

        verify(todoService).isTodoExist(todoId);
        verify(todoRepository).findById(todoId);
        verify(todoService).overwriteTodoWithTask(eq(todo), anyList());
        verify(todoUtil).getDescription(null, "newName", 1);
        verifyNoMoreInteractions(todoUtil);
    }

    @Test
    void shouldReturnErrorResponseWhenPersistenceFails() throws TodoPersistenceException {
        // given
        Long todoId = 3L;
        Todo todo = new Todo();
        todo.setId(todoId);

        TodoRequest todoRequest = new TodoRequest("someName", "someDesc", List.of());
        RequestConfig request = new RequestConfig(Pair.of(RequestKey.TODO_ID, todoId),
                Pair.of(RequestKey.TODO_REQUEST_OBJ, todoRequest));

        when(todoService.isTodoExist(todoId)).thenReturn(true);
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));
        when(todoUtil.getDescription("someDesc", "someName", 0)).thenReturn("someDesc");

        when(todoService.overwriteTodoWithTask(eq(todo), anyList()))
                .thenThrow(new TodoPersistenceException("DB error"));

        when(todoUtil.createErrorResponse("DB error", 500))
                .thenReturn(createError("DB error", 500));

        // when
        TodoResponse result = updateTodoWithTaskCmd.execute(request);

        // then
        assertEquals("DB error", result.getErrorMessage());
        assertEquals(500, result.getErrorCode());

        verify(todoService).isTodoExist(todoId);
        verify(todoRepository).findById(todoId);
        verify(todoService).overwriteTodoWithTask(eq(todo), anyList());
        verify(todoUtil).createErrorResponse("DB error", 500);
    }

    private TodoResponse createError(String msg, int code) {
        TodoResponse response = new TodoResponse();
        response.setErrorCode(code);
        response.setErrorMessage(msg);
        return response;
    }
}