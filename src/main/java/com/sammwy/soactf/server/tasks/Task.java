package com.sammwy.soactf.server.tasks;

public class Task {
    private Runnable runnable;
    private long delay;
    private long lastExecution;
    private TaskType type;

    public Task(Runnable runnable, long delay, TaskType type) {
        this.runnable = runnable;
        this.delay = delay;
        this.lastExecution = 0;
        this.type = type;
    }

    public void cancel() {
        this.delay = -1;
    }

    public long getDelay() {
        return this.delay;
    }

    public long getLastExecution() {
        return this.lastExecution;
    }

    public TaskType getType() {
        return this.type;
    }

    public boolean isCancelled() {
        return this.delay == -1;
    }

    public boolean shouldRun(long currentTick) {
        return currentTick >= this.lastExecution + this.delay;
    }

    public void run() {
        this.runnable.run();
    }

    public void run(long currentTick) {
        this.run();
        this.lastExecution = currentTick;
    }
}
