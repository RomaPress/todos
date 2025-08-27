package com.roman.pres.todos.service.command;

import com.roman.pres.todos.model.config.RequestConfig;

public interface Command<T> {
    CommandKey getName();

    T execute(RequestConfig config);
}
