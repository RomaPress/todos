package com.roman.pres.todos.service.command;

import com.roman.pres.todos.model.config.RequestConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommandExecutor {
    private final Map<CommandKey, Command> commandMap;

    public <T> T execute(CommandKey key, RequestConfig config) {
        Command<T> cmd = (Command<T>)  commandMap.get(key);
        if (cmd == null) {
            throw new RuntimeException("Command not found" + key);
        }
        return cmd.execute(config);
    }
}
