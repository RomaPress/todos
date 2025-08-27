package com.roman.pres.todos.service.command.impl;

import com.roman.pres.todos.model.config.RequestConfig;
import com.roman.pres.todos.model.dao.TaskCollection;
import com.roman.pres.todos.model.dao.Todo;
import com.roman.pres.todos.model.dao.TodoTask;
import com.roman.pres.todos.repository.TodoRepository;
import com.roman.pres.todos.service.command.CommandKey;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllTodoCmdTest {
    @Mock
    private TodoRepository todoRepository;
    @InjectMocks
    private GetAllTodoCmd getAllTodoCmd;

    @Test
    void shouldReturnListOfTodoResponse(){
        //given
        var task1 = new TodoTask("task1", "desc1");

        var taskCollection1 = new TaskCollection();
        taskCollection1.setTodoTask(task1);

        var todo1 = new Todo(1L, "todo1", "desc1");
        var todo2 = new Todo(2L, "todo2", "desc2");
        todo1.getTaskCollections().add(taskCollection1);

        var todoList = List.of(todo1, todo2);

        when(todoRepository.findAll()).thenReturn(todoList);
        //when
        var response = getAllTodoCmd.execute(RequestConfig.EMPTY);

        //than
        assertThat(response).isNotNull();
        assertThat(response).isNotEmpty();

        var firstTodo = response.get(0);
        assertThat(firstTodo.getId()).isEqualTo(1L);
        assertThat(firstTodo.getName()).isEqualTo("todo1");
        assertThat(firstTodo.getDescription()).isEqualTo("desc1");
        assertThat(firstTodo.getTasks()).hasSize(1);
        var taskRes = firstTodo.getTasks().get(0);
        assertThat(taskRes.getName()).isEqualTo("task1");
        assertThat(taskRes.getDescription()).isEqualTo("desc1");

        var secondTodo = response.get(1);
        assertThat(secondTodo.getId()).isEqualTo(2L);
        assertThat(secondTodo.getName()).isEqualTo("todo2");
        assertThat(secondTodo.getDescription()).isEqualTo("desc2");
        assertThat(secondTodo.getTasks()).hasSize(0);
    }

    @Test
    void shouldReturnEmptyList_WhenNoTodo(){
        //given
        when(todoRepository.findAll()).thenReturn(new ArrayList<>());
        //when
        var response = getAllTodoCmd.execute(RequestConfig.EMPTY);
        //than
        assertThat(response).isNotNull();
        assertThat(response).isEmpty();
    }

    @Test
    void shouldReturnCorrectCommandName() {
        assertThat(getAllTodoCmd.getName()).isEqualTo(CommandKey.GET_ALL_TODO);
    }
}