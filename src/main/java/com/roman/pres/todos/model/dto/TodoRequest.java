package com.roman.pres.todos.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TodoRequest {
    private String name;
    private String description;
    private List<TaskRequest> tasks = new ArrayList<>();
}
