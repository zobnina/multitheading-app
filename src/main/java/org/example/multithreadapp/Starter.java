package org.example.multithreadapp;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class Starter {
    private static final Logger LOG = LogManager.getLogger(Starter.class);
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String PROPERTIES_PATH = USER_DIR + "/src/main/resources/work.properties";
    private static int startInterval;
    private static int interval;
    private static int workerCount;
    private static int maxTasksCount;
    private static int countTasksForDay;
    private static int serversCount;

    static {
        File file = new File(PROPERTIES_PATH);
        try (FileReader reader = new FileReader(file)) {
            Properties properties = new Properties();
            properties.load(reader);
            startInterval = Integer.parseInt(properties.getProperty("startInterval"));
            interval = Integer.parseInt(properties.getProperty("interval"));
            workerCount = Integer.parseInt(properties.getProperty("workerCount"));
            maxTasksCount = Integer.parseInt(properties.getProperty("maxTasksCount"));
            countTasksForDay = Integer.parseInt(properties.getProperty("tasksCountForDay"));
            serversCount = Integer.parseInt(properties.getProperty("serversCount"));
        } catch (Exception e) {
            LOG.error(e.toString());
        }
    }

    private static BlockingQueue<Task> tasks;

    public static void main(String[] args) {
        tasks = new ArrayBlockingQueue<>(maxTasksCount);
        AtomicInteger allDone = new AtomicInteger();
        Semaphore servers = new Semaphore(serversCount);
        startManager();
    }

    private static void startManager() {
        FutureTask<Integer> managerFuture = new FutureTask<>(new Manager(tasks));
        new Thread(managerFuture).start();
    }
}
