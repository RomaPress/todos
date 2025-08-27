package com.roman.pres.todos.model.config;

import com.roman.pres.todos.model.dto.TodoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import static org.junit.jupiter.api.Assertions.*;

class RequestConfigTest {
    @Test
    void getLong_shouldReturnValue_whenKeyExistsAndTypeCorrect() {
        RequestConfig config = new RequestConfig(Pair.of(RequestKey.TODO_ID, 42L));

        Long result = config.getLong(RequestKey.TODO_ID);

        assertEquals(42L, result);
    }

    @Test
    void getLong_shouldThrowClassCastException_whenValueNotLong() {
        RequestConfig config = new RequestConfig(Pair.of(RequestKey.TODO_ID, "notLong"));

        assertThrows(ClassCastException.class, () -> config.getLong(RequestKey.TODO_ID));
    }

    @Test
    void getLong_shouldThrowRuntimeException_whenKeyNotFound() {
        RequestConfig config = new RequestConfig();

        assertThrows(RuntimeException.class, () -> config.getLong(RequestKey.TODO_ID));
    }

    @Test
    void getObject_shouldReturnValue_whenKeyExistsAndTypeCorrect() {
        TodoRequest todoRequest = new TodoRequest();
        RequestConfig config = new RequestConfig(Pair.of(RequestKey.TODO_REQUEST_OBJ, todoRequest));

        TodoRequest result = config.getObject(RequestKey.TODO_REQUEST_OBJ, TodoRequest.class);

        assertEquals(todoRequest, result);
    }

    @Test
    void getObject_shouldThrowClassCastException_whenValueNotMatchingClass() {
        RequestConfig config = new RequestConfig(Pair.of(RequestKey.TODO_REQUEST_OBJ, "notTodoRequest"));

        assertThrows(ClassCastException.class, () -> config.getObject(RequestKey.TODO_REQUEST_OBJ, TodoRequest.class));
    }

    @Test
    void getObject_shouldThrowRuntimeException_whenKeyNotFound() {
        RequestConfig config = new RequestConfig();

        assertThrows(RuntimeException.class, () -> config.getObject(RequestKey.TODO_REQUEST_OBJ, TodoRequest.class));
    }
}