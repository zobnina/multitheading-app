package org.example.multithreadapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Developer implements Callable<Integer> {
    private static final Logger LOG = LogManager.getLogger(Developer.class);
    private static final int SECOND = 1000;

    private final String workerName;
    private final BlockingQueue<Task> tasks;
    private int done;
    private final AtomicInteger allDone;
    private final Semaphore servers;
    private final int countTasksForDay;

    public Developer(String name, BlockingQueue<Task> tasks, Semaphore servers, AtomicInteger allDone, int countTasksForDay) {
        this.workerName = name;
        this.tasks = tasks;
        this.servers = servers;
        this.allDone = allDone;
        this.countTasksForDay = countTasksForDay;
        done = 0;
    }

    int getDone() {
        return done;
    }

    private void doWork(Task currentTask) throws InterruptedException {
        if (currentTask instanceof Report) {
            develop(currentTask);
        } else {
            developAndTest(currentTask);
        }
    }

    private void developAndTest(Task currentTask) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        develop(currentTask);
        test(currentTask);
        done++;
        allDone.getAndIncrement();
        long diffTime = System.currentTimeMillis() - startTime;
        String message = String.format("%s: The task \"%s\" of complexity %d was completed and tested in the duration of %d seconds", workerName, currentTask.getName(), currentTask.getComplexity(), diffTime / SECOND);
        LOG.info(message);
    }

    private void test(Task currentTask) throws InterruptedException {
        String message;
        long startTime = System.currentTimeMillis();
        servers.acquire();
        long diffTime = System.currentTimeMillis() - startTime;
        if (diffTime >= SECOND) {
            message = String.format("%s: Finally free server! Less than %d seconds have passed", workerName, diffTime / SECOND);
        } else {
            message = String.format("%s: A free server didn't even have to wait!", workerName);
        }
        LOG.info(message);
        long pauseTime = (long) currentTask.getComplexity() * 2 * SECOND;
        Thread.sleep(pauseTime);
        servers.release();
    }

    private void develop(Task currentTask) throws InterruptedException {
        long pauseTime = (long) currentTask.getComplexity() * SECOND;
        Thread.sleep(pauseTime);
        String message = String.format("%s: completed %d tasks out of the total number %d", workerName, done, allDone.get());
        LOG.info(message);
    }

    @Override
    public Integer call() throws Exception {
        try {
            String message = String.format("%s started working", workerName);
            LOG.info(message);
            while (done < countTasksForDay) {
                Task currentTask = tasks.poll();
                if (currentTask != null) {
                    message = String.format("%s: I got task \"%s\"", workerName, currentTask.getName());
                    LOG.info(message);
                    doWork(currentTask);
                }
            }
            message = String.format("%s: completed %d tasks and my working day is over", workerName, countTasksForDay);
            LOG.info(message);
        }
        catch (Exception e){
            String message = String.format("%s: The working day is over", workerName);
            LOG.info(message);
        }
        return done;
    }
}
