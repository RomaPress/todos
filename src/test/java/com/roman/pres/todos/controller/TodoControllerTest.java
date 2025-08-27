package com.roman.pres.todos.controller;

import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.dto.ApiResponse;
import com.roman.pres.todos.model.dto.TodoRequest;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.service.command.CommandExecutor;
import com.roman.pres.todos.service.command.CommandKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TodoControllerTest {
    @Mock
    private CommandExecutor commandExecutor;

    @InjectMocks
    private TodoController todoController;

    @Test
    void obtainTodo_shouldReturnTodoResponse() {
        // given
        Long todoId = 1L;
        TodoResponse mockResponse = new TodoResponse();
        mockResponse.setId(todoId);

        when(commandExecutor.execute(eq(CommandKey.GET_TODO_BY_ID), any(RequestConfig.class)))
                .thenReturn(mockResponse);

        // when
        ResponseEntity<ApiResponse> response = todoController.obtainTodo(todoId);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(commandExecutor).execute(eq(CommandKey.GET_TODO_BY_ID), any(RequestConfig.class));
    }

    @Test
    void deleteTodo_shouldReturnNoContent_whenSuccess() {
        // given
        Long todoId = 2L;
        TodoResponse mockResponse = new TodoResponse();
        when(commandExecutor.execute(eq(CommandKey.DELETE_TODO), any(RequestConfig.class)))
                .thenReturn(mockResponse);

        // when
        ResponseEntity<ApiResponse> response = todoController.deleteTodo(todoId);

        // then
        assertEquals(204, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(commandExecutor).execute(eq(CommandKey.DELETE_TODO), any(RequestConfig.class));
    }

    @Test
    void obtainTodo_shouldReturnError_whenCommandReturnsError() {
        // given
        Long todoId = 3L;
        TodoResponse errorResponse = new TodoResponse();
        errorResponse.setErrorMessage("Not found");
        errorResponse.setErrorCode(404);

        when(commandExecutor.execute(eq(CommandKey.GET_TODO_BY_ID), any(RequestConfig.class)))
                .thenReturn(errorResponse);

        // when
        ResponseEntity<ApiResponse> response = todoController.obtainTodo(todoId);

        // then
        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApiResponse);
        ApiResponse body = (ApiResponse) response.getBody();
        assertEquals("Not found", body.getErrorMessage());
        assertEquals(404, body.getErrorCode());
    }

    @Test
    void obtainAllTodos_shouldReturnList() {
        // given
        TodoResponse todo1 = new TodoResponse();
        TodoResponse todo2 = new TodoResponse();
        List<TodoResponse> todos = List.of(todo1, todo2);
        when(commandExecutor.execute(eq(CommandKey.GET_ALL_TODO), any(RequestConfig.class)))
                .thenReturn(todos);

        // when
        List<TodoResponse> response = todoController.obtainAllTodos();

        // then
        assertEquals(2, response.size());
        verify(commandExecutor).execute(eq(CommandKey.GET_ALL_TODO), any(RequestConfig.class));
    }

    @Test
    void createTodo_shouldReturnError_whenCommandReturnsError() {
        // given
        TodoRequest request = new TodoRequest();
        TodoResponse errorResponse = new TodoResponse();
        errorResponse.setErrorMessage("Validation failed");
        errorResponse.setErrorCode(400);
        when(commandExecutor.execute(eq(CommandKey.CREATE_TODO), any(RequestConfig.class)))
                .thenReturn(errorResponse);

        // when
        ResponseEntity<ApiResponse> response = todoController.createTodo(request);

        // then
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApiResponse);
        ApiResponse body = (ApiResponse) response.getBody();
        assertEquals("Validation failed", body.getErrorMessage());
        assertEquals(400, body.getErrorCode());
    }

    @Test
    void deleteTodo_shouldReturnError_whenCommandReturnsError() {
        // given
        Long todoId = 4L;
        TodoResponse errorResponse = new TodoResponse();
        errorResponse.setErrorMessage("Cannot delete");
        errorResponse.setErrorCode(403);
        when(commandExecutor.execute(eq(CommandKey.DELETE_TODO), any(RequestConfig.class)))
                .thenReturn(errorResponse);

        // when
        ResponseEntity<ApiResponse> response = todoController.deleteTodo(todoId);

        // then
        assertEquals(403, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApiResponse);
        ApiResponse body = (ApiResponse) response.getBody();
        assertEquals("Cannot delete", body.getErrorMessage());
        assertEquals(403, body.getErrorCode());
    }

    @Test
    void overwriteTodo_shouldReturnUpdatedTodo() {
        // given
        Long todoId = 5L;
        TodoRequest request = new TodoRequest();
        TodoResponse mockResponse = new TodoResponse();
        mockResponse.setId(todoId);
        when(commandExecutor.execute(eq(CommandKey.UPDATE_TODO_WITH_TASK), any(RequestConfig.class)))
                .thenReturn(mockResponse);

        // when
        ResponseEntity<?> response = todoController.overwriteTodo(todoId, request);

        // then
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockResponse, response.getBody());
        verify(commandExecutor).execute(eq(CommandKey.UPDATE_TODO_WITH_TASK), any(RequestConfig.class));
    }

    @Test
    void overwriteTodo_shouldReturnError_whenCommandReturnsError() {
        // given
        Long todoId = 6L;
        TodoRequest request = new TodoRequest();
        TodoResponse errorResponse = new TodoResponse();
        errorResponse.setErrorMessage("Update failed");
        errorResponse.setErrorCode(400);
        when(commandExecutor.execute(eq(CommandKey.UPDATE_TODO_WITH_TASK), any(RequestConfig.class)))
                .thenReturn(errorResponse);

        // when
        ResponseEntity<?> response = todoController.overwriteTodo(todoId, request);

        // then
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof ApiResponse);
        ApiResponse body = (ApiResponse) response.getBody();
        assertEquals("Update failed", body.getErrorMessage());
        assertEquals(400, body.getErrorCode());
    }
}