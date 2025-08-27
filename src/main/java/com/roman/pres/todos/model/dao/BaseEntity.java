package com.roman.pres.todos.model.dao;

import java.io.Serializable;

public interface BaseEntity<T extends Serializable> {
    void setId(T id);

    T getId();
}
