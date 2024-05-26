package com.outstandingboy.donationalert.common.event;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

class TopicTest {
    @Test
    void subscribe_single() {
        AtomicReference<String> ref = new AtomicReference<>();
        try (Topic<String> topic = new Topic<>()) {
            topic.subscribe(ref::set);
            topic.publish("test");
        }
        Assertions.assertEquals(ref.get(), "test");
    }

    @Test
    void subscribe_multiple() {
        AtomicReference<String> ref1 = new AtomicReference<>();
        AtomicReference<String> ref2 = new AtomicReference<>();
        AtomicReference<String> ref3 = new AtomicReference<>();
        try (Topic<String> topic = new Topic<>()) {
            topic.subscribe(ref1::set);
            topic.subscribe(ref2::set);
            topic.subscribe(ref3::set);

            topic.publish("test");
        }
        Assertions.assertEquals(ref1.get(), "test");
        Assertions.assertEquals(ref2.get(), "test");
        Assertions.assertEquals(ref3.get(), "test");
    }

    @Test
    void subscribe_exception_dont_block() {
        AtomicReference<String> ref = new AtomicReference<>();
        try (Topic<String> topic = new Topic<>()) {
            topic.subscribe(s -> {
                throw new RuntimeException();
            });
            topic.subscribe(ref::set);

            topic.publish("test");
        }
        Assertions.assertEquals(ref.get(), "test");
    }

    @Test
    void publish_multiple() {
        CountDownLatch latch = new CountDownLatch(3);
        try (Topic<String> topic = new Topic<>()) {
            topic.subscribe(s -> latch.countDown());
            topic.publish("test");
            topic.publish("test");
            topic.publish("test");
        }
        Assertions.assertEquals(latch.getCount(), 0);
    }

    @Test
    void unsubscribe() {
        AtomicReference<String> ref = new AtomicReference<>();
        try (Topic<String> topic = new Topic<>()) {
            String id = topic.subscribe(ref::set);
            topic.unsubscribe(id);
            topic.publish("test");
        }
        Assertions.assertNull(ref.get());
    }

    @Test
    void close() {
        AtomicReference<String> ref = new AtomicReference<>();
        try (Topic<String> topic = new Topic<>()) {
            topic.subscribe(ref::set);
            topic.subscribe(ref::set);
            topic.subscribe(ref::set);
            topic.subscribe(ref::set);
            topic.subscribe(ref::set);
            topic.close();
            topic.publish("test");
        }
        Assertions.assertNull(ref.get());
    }
}