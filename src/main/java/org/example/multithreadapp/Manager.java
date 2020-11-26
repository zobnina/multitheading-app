package org.example.multithreadapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class Manager implements Callable<Integer> {
    private static final Logger LOG = LogManager.getLogger(Manager.class);
    private static final String QUIT = "quit";

    private final BlockingQueue<Task> tasks;
    private int countTasks;

    public Manager(BlockingQueue<Task> tasks) {
        this.tasks = tasks;
    }

    public Integer call() throws Exception {
        LOG.info("Manager started: ");
        while (true) {
            Task task = requestTask();
            if (task.getName().equals(QUIT)) break;
        }
        LOG.info("The working day is over. Thank you for using our services!");
        return countTasks;
    }

    private Task requestTask() {
        String name = getTaskName();
        if (!name.equals(QUIT)) {
            int complexity = getTaskComplexity();
            Task task = new Task(name, complexity);
            if (tasks.offer(task)) {
                countTasks++;
                return task;
            } else {
                String message = String.format("Task queue is filled. Task \"%s\" is not added.", task.getName());
                LOG.warn(message);
            }
        }
        return new Task(name, 0);
    }

    private String getTaskName() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter task name: ");
        return scanner.nextLine();
    }

    private int getTaskComplexity() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter task complexity: ");
        return scanner.nextInt();
    }
}
