package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.exception.TodoPersistenceException;
import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.config.RequestKey;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dto.TaskRequest;
import com.roman.pres.todos.model.dto.TodoRequest;
import com.roman.pres.todos.model.dto.TodoResponse;
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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CreateTodoCmdTest {
    @Mock
    private TodoService todoService;
    @Mock
    private TodoUtil todoUtil;
    @InjectMocks
    private CreateTodoCmd createTodoCmd;
    private RequestConfig request;

    @BeforeEach
    void setUpRequest(){
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("task");
        taskRequest.setDescription("taskDescription");

        TodoRequest todoRequest = new TodoRequest();
        todoRequest.setName("todo");
        todoRequest.setDescription("todoDescription");
        todoRequest.setTasks(List.of(taskRequest));

        request = new RequestConfig(Pair.of(RequestKey.TODO_REQUEST_OBJ, todoRequest));
    }

    @Test
    void shouldReturnSuccessResponse_WhenSaveSuccess() throws TodoPersistenceException {
        //given
        Todo savedTodo = new Todo();
        savedTodo.setId(1L);
        savedTodo.setName("todo");
        savedTodo.setDescription("todoDescription");

        when(todoService.saveTodoWithTask(any(Todo.class), any()))
                .thenReturn(savedTodo);
        //when
        TodoResponse response = createTodoCmd.execute(request);

        //than
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("todo");
        assertThat(response.getDescription()).isEqualTo("todoDescription");
    }

    @Test
    void shouldReturnErrorResponse_WhenSaveFailed() throws TodoPersistenceException {
        //given
        TodoResponse errorResponse = new TodoResponse();
        errorResponse.setErrorMessage("Error");
        errorResponse.setErrorCode(500);

        when(todoService.saveTodoWithTask(any(Todo.class), any()))
                .thenThrow(new TodoPersistenceException("Error"));
        when(todoUtil.createErrorResponse("Error", 500))
                .thenReturn(errorResponse);
        //when
        TodoResponse response = createTodoCmd.execute(request);
        //than
        assertThat(response.getErrorMessage()).isEqualTo("Error");
        assertThat(response.getErrorCode()).isEqualTo(500);
    }

    @Test
    void shouldReturnCorrectCommandName() {
        assertThat(createTodoCmd.getName()).isEqualTo(CommandKey.CREATE_TODO);
    }
}