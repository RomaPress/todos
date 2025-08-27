package com.roman.pres.todos.util;

import com.roman.pres.todos.model.dto.TodoResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TodoUtil {
    private static final String DESCRIPTION_TEMPLATE = "%s consist of %s task";

    public TodoResponse createErrorResponse(String message, int errorCode) {
        TodoResponse errorResponse = new TodoResponse();
        errorResponse.setErrorMessage(message);
        errorResponse.setErrorCode(errorCode);
        return errorResponse;
    }

    public String getDescription(String desc, String name, int taskAmount) {
        if (StringUtils.isBlank(desc)) {
            return String.format(DESCRIPTION_TEMPLATE, name, taskAmount);
        }
        return desc;
    }
}
