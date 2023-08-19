package com.sammwy.soactf.server.events;

public class Cancellable extends Event {
    protected boolean cancelled = false;

    public boolean isCancelled() {
        return this.cancelled;
    }

    public void cancel() {
        this.cancelled = true;
    }
}
