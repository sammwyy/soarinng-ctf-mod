package com.sammwy.soactf.server.tasks;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TaskScheduler {
    private long currentTick;
    private List<Task> tasks;

    public TaskScheduler() {
        this.currentTick = 0;
        this.tasks = new ArrayList<>();
    }

    public void runTask(Runnable runnable, long delay) {
        this.tasks.add(new Task(runnable, delay, TaskType.DELAYED));
    }

    public void runTaskRepeat(Runnable runnable, long delay) {
        this.tasks.add(new Task(runnable, delay, TaskType.REPEATING));
    }

    public void cancelAll() {
        this.tasks.clear();
    }

    public long getCurrentTick() {
        return this.currentTick;
    }

    public void tick() {
        this.currentTick++;

        Iterator<Task> iterator = this.tasks.iterator();

        while (iterator.hasNext()) {
            Task task = iterator.next();

            if (task.isCancelled()) {
                iterator.remove();
                continue;
            }

            if (task.shouldRun(this.currentTick)) {
                task.run(this.currentTick);

                if (task.getType() == TaskType.DELAYED) {
                    iterator.remove();
                }
            }
        }
    }
}
