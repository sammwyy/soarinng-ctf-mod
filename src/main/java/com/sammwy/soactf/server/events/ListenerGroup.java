package com.sammwy.soactf.server.events;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ListenerGroup<T> {
    private List<Consumer<T>> listeners;

    public ListenerGroup() {
        this.listeners = new ArrayList<>();
    }

    public void addListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }

    @SuppressWarnings("unchecked")
    public void addUnsafeListener(Consumer<?> listener) {
        this.listeners.add((Consumer<T>) listener);
    }

    public void removeListener(Consumer<T> listener) {
        this.listeners.remove(listener);
    }

    public List<Consumer<T>> getListeners() {
        return this.listeners;
    }

    public T call(T event) {
        for (Consumer<T> listener : this.listeners) {
            listener.accept(event);

            if (event instanceof Cancellable) {
                if (((Cancellable) event).isCancelled()) {
                    break;
                }
            }

        }

        return event;
    }
}
