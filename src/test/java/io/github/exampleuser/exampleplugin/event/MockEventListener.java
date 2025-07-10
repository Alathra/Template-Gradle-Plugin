package io.github.exampleuser.exampleplugin.event;

@FunctionalInterface
public interface MockEventListener {
    void onEvent(MockEvent event);
}