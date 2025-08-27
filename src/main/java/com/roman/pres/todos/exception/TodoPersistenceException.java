package com.roman.pres.todos.exception;

public class TodoPersistenceException extends Exception  {
    public TodoPersistenceException(String message, Throwable cause) {
        super(message, cause);
    }

    public TodoPersistenceException(String message) {
        super(message);
    }

    public TodoPersistenceException(Throwable cause) {
        super(cause);
    }
}
