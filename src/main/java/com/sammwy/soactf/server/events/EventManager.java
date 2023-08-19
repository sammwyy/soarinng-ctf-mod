package com.sammwy.soactf.server.events;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventManager {
    private Map<Class<?>, ListenerGroup<?>> listeners;
    private static EventManager INSTANCE;

    public EventManager() {
        this.listeners = new HashMap<>();
        INSTANCE = this;
    }

    public void unsafeSubscribe(Class<?> eventType, Consumer<?> listener) {
        ListenerGroup<?> group = (ListenerGroup<?>) this.listeners.get(eventType);

        if (group == null) {
            group = new ListenerGroup<>();
            this.listeners.put(eventType, group);
        }

        group.addUnsafeListener(listener);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void subscribe(Class<E> eventType, Consumer<E> listener) {
        ListenerGroup<E> group = (ListenerGroup<E>) this.listeners.get(eventType);

        if (group == null) {
            group = new ListenerGroup<>();
            this.listeners.put(eventType, group);
        }

        group.addListener(listener);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void unsubscribe(Class<E> eventType, Consumer<E> listener) {
        ListenerGroup<E> group = (ListenerGroup<E>) this.listeners.get(eventType);

        if (group == null) {
            return;
        }

        group.removeListener(listener);
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> List<Consumer<E>> getListeners(Class<E> eventType) {
        ListenerGroup<E> group = (ListenerGroup<E>) this.listeners.get(eventType);

        if (group == null) {
            return null;
        }

        return group.getListeners();
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> E call(Event event) {
        ListenerGroup<E> group = (ListenerGroup<E>) this.listeners.get(event.getClass());

        if (group == null) {
            return (E) event;
        }

        return group.call((E) event);
    }

    public static <E extends Event> E staticCall(Event event) {
        return INSTANCE.call(event);
    }

    public void subscribeAll(Object instance) {
        Class<?> clazz = instance.getClass();

        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent(Listener.class)) {
                Class<?> eventType = method.getParameterTypes()[0];
                Consumer<? extends Event> consumer = e -> {
                    try {
                        method.invoke(instance, e);
                    } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                };

                this.unsafeSubscribe(eventType, consumer);
            }
        }
    }
}
