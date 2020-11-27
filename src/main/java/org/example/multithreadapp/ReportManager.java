package org.example.multithreadapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class ReportManager implements Callable<Integer> {
    private static final Logger LOG = LogManager.getLogger(ReportManager.class);
    private static final int MAX_COMPLEXITY = 5;
    private static final int TIME_FOR_CREATING_REPORT = 5000;

    private int number;
    private final BlockingQueue<Task> tasks;
    private final Random random;


    public ReportManager(BlockingQueue<Task> tasks) {
        this.tasks = tasks;
        number = 0;
        random = new Random();
    }

    @Override
    public Integer call() throws Exception {
        LOG.info("Leader started working");
        createReportTask();
        return number;
    }

    private void createReportTask() throws InterruptedException {
        Thread.sleep(TIME_FOR_CREATING_REPORT);
        int complexity = random.nextInt(MAX_COMPLEXITY);
        if (tasks.offer(new Report(number, complexity))) {
            number++;
        } else {
            String message = "Task queue is filled. Report is not added.";
            LOG.warn(message);
        }
    }
}
