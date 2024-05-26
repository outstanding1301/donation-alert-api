package com.outstandingboy.donationalert.common.event;

import java.io.Closeable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class Topic<T> implements Closeable {
    private final Map<String, Consumer<T>> listeners;

    public Topic() {
        listeners = new LinkedHashMap<>();
    }

    public void publish(T message) {
        listeners.values().forEach(consumer -> {
            try {
                consumer.accept(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String subscribe(String id, Consumer<T> listener) {
        listeners.put(id, listener);
        return id;
    }

    public String subscribe(Consumer<T> listener) {
        return subscribe(UUID.randomUUID().toString(), listener);
    }

    public void unsubscribe(String id) {
        listeners.remove(id);
    }

    @Override
    public void close() {
        listeners.clear();
    }
}
