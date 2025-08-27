package com.roman.pres.todos.util;

import com.roman.pres.todos.model.dto.TodoResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TodoUtilTest {
    private final TodoUtil todoUtil = new TodoUtil();

    @Test
    void shouldReturnProvidedDescription_WhenNotBlank() {
        String desc = "This is a test";
        String result = todoUtil.getDescription(desc, "todo", 5);

        assertEquals(desc, result);
    }

    @Test
    void shouldGenerateDescription_WhenBlankProvided() {
        String result = todoUtil.getDescription("", "todo", 5);

        assertEquals("todo consist of 5 task", result);
    }

    @Test
    void createErrorResponse() {
        // given
        String message = "Some error";
        int code = 500;

        // when
        TodoResponse response = todoUtil.createErrorResponse(message, code);

        // then
        assertNotNull(response);
        assertEquals(message, response.getErrorMessage());
        assertEquals(code, response.getErrorCode());
    }
}