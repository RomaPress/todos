package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.service.command.CommandKey;
import com.roman.pres.todos.util.TodoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTodoCmdTest {
    @Mock
    private TodoRepository todoRepository;
    @Mock
    private TodoUtil todoUtil;
    @InjectMocks
    private GetTodoCmd getTodoCmd;

    private RequestConfig request;

    @BeforeEach
    void setUpRequest() {
        request = new RequestConfig(Pair.of(RequestKey.TODO_ID, 1L));
    }

    @Test
    void execute_ShouldReturnTodoResponse_WhenTodoExists() {
        // given
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setName("Test Todo");
        when(todoRepository.findById(1L)).thenReturn(Optional.of(todo));

        // when
        TodoResponse response = getTodoCmd.execute(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Test Todo");
    }

    @Test
    void execute_ShouldReturnErrorResponse_WhenTodoNotFound() {
        // given
        when(todoRepository.findById(1L)).thenReturn(Optional.empty());

        TodoResponse errorResponse = createError("Todo with id 1 not found", HttpStatus.NOT_FOUND.value());
        when(todoUtil.createErrorResponse("Todo with id 1 not found", HttpStatus.NOT_FOUND.value()))
                .thenReturn(errorResponse);

        // when
        TodoResponse response = getTodoCmd.execute(request);

        // then
        assertThat(response.getErrorMessage()).isEqualTo("Todo with id 1 not found");
        assertThat(response.getErrorCode()).isEqualTo(HttpStatus.NOT_FOUND.value());
        verify(todoRepository).findById(1L);
        verify(todoUtil).createErrorResponse("Todo with id 1 not found", HttpStatus.NOT_FOUND.value());
    }

    @Test
    void getName_ShouldReturnCorrectCommandKey() {
        assertThat(getTodoCmd.getName()).isEqualTo(CommandKey.GET_TODO_BY_ID);
    }

    private TodoResponse createError(String msg, int code) {
        TodoResponse response = new TodoResponse();
        response.setErrorCode(code);
        response.setErrorMessage(msg);
        return response;
    }
}