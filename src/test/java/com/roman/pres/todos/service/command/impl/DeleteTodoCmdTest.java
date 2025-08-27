package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.exception.TodoPersistenceException;
import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dto.TodoResponse;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.service.TodoService;
import com.roman.pres.todos.service.command.CommandKey;
import com.roman.pres.todos.util.TodoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Pair;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteTodoCmdTest {
    @Mock
    private TodoService todoService;
    @Mock
    private TodoUtil todoUtil;
    @InjectMocks
    private DeleteTodoCmd deleteTodoCmd;

    private RequestConfig request;


    @BeforeEach
    void setUpRequest() {
        request = new RequestConfig(Pair.of(RequestKey.TODO_ID, 1L));
    }

    @Test
    void shouldReturnError_WhenTodoDoesNotExist(){
        //given
        when(todoService.isTodoExist(1L)).thenReturn(false);
        when(todoUtil.createErrorResponse("Todo with id = 1 not found",404))
                .thenReturn(createError("Todo with id = 1 not found",404));

        //when
        TodoResponse response = deleteTodoCmd.execute(request);

        //than
        assertThat(response.getErrorMessage()).isEqualTo("Todo with id = 1 not found");
        assertThat(response.getErrorCode()).isEqualTo(404);
    }

    @Test
    void shouldReturnSuccess_WhenTodoDeletedSuccessfully() throws TodoPersistenceException {
        //given
        Todo deleted = new Todo();
        deleted.setId(1L);
        deleted.setName("name");

        when(todoService.isTodoExist(1L)).thenReturn(true);
        when(todoService.cleanupTodoWithTask(1L)).thenReturn(deleted);

        //when
        TodoResponse response = deleteTodoCmd.execute(request);

        //than
        assertThat(response.getErrorMessage()).isNull();
        assertThat(response.getErrorCode()).isNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("name");
    }

    @Test
    void shouldReturnError_WhenDeleteFailed() throws TodoPersistenceException {
        //given
        when(todoService.isTodoExist(1L)).thenReturn(true);
        when(todoService.cleanupTodoWithTask(1L))
                .thenThrow(new TodoPersistenceException("Error"));
        when(todoUtil.createErrorResponse("Error",500))
                .thenReturn(createError("Error",500));

        //when
        TodoResponse response = deleteTodoCmd.execute(request);

        //than
        assertThat(response.getErrorMessage()).isEqualTo("Error");
        assertThat(response.getErrorCode()).isEqualTo(500);
    }

    @Test
    void shouldReturnCorrectCommandName() {
        assertThat(deleteTodoCmd.getName()).isEqualTo(CommandKey.DELETE_TODO);
    }

    private TodoResponse createError(String msg, int code) {
        TodoResponse response = new TodoResponse();
        response.setErrorCode(code);
        response.setErrorMessage(msg);
        return response;
    }
}