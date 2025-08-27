package com.roman.pres.todos.model.config;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
public class RequestConfig {
    public static final RequestConfig EMPTY = new RequestConfig();
    private Map<RequestKey, Object> params = new HashMap<>();

    @SafeVarargs
    public RequestConfig(Pair<RequestKey, Object>... paramArgs) {
        Arrays.stream(paramArgs)
                .forEach(stringObjectPair -> {
                    this.params.put(stringObjectPair.getFirst(), stringObjectPair.getSecond());
                });
    }

    public Long getLong(RequestKey key) {
        if (params.containsKey(key)) {
            var value = this.params.get(key);
            if (value instanceof Long) {
                return (Long) value;
            }
            throw new ClassCastException(key + " is not a Long");
        }
        throw new RuntimeException("Key " + key + " not found");
    }

    public <T> T getObject(RequestKey key, Class<T> clazz) {
        if (params.containsKey(key)) {
            var value = this.params.get(key);
            if (clazz.isInstance(value)) {
                return clazz.cast(value);
            }
            throw new ClassCastException(key + " is not a " + clazz.getCanonicalName());
        }
        throw new RuntimeException("Key " + key + " not found");
    }
}
