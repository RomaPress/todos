package com.roman.pres.todos.service.command;

import com.roman.pres.todos.model.config.RequestConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandExecutorTest {
    @Mock
    private Command<String> commandMock;

    @InjectMocks
    private CommandExecutor executor;

    @BeforeEach
    void setUp() {
        executor = new CommandExecutor(Map.of(CommandKey.GET_TODO_BY_ID, commandMock));
    }

    @Test
    void execute_commandFound_shouldReturnResult() {
        // given
        RequestConfig config = new RequestConfig();
        when(commandMock.execute(config)).thenReturn("result");

        // when
        String result = executor.execute(CommandKey.GET_TODO_BY_ID, config);

        // then
        assertEquals("result", result);
        verify(commandMock).execute(config);
    }

    @Test
    void execute_commandNotFound_shouldThrowException() {
        RequestConfig config = new RequestConfig();

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> executor.execute(CommandKey.CREATE_TODO, config));

        assertTrue(ex.getMessage().contains("Command not found"));
    }
}