package com.roman.pres.todos.configuration;

import com.roman.pres.todos.service.command.Command;
import com.roman.pres.todos.service.command.CommandKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class ServiceCommand {
    @Bean
    public Map<CommandKey, Command> getCommandMap(List<Command> commands) {
        return commands.stream()
                .collect(Collectors.toMap(Command::getName, command -> command));
    }
}
