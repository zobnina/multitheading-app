package org.example.multithreadapp;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.*;
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
    private static AtomicInteger allDone;
    private static Semaphore servers;
    

    public static void main(String[] args) {
        tasks = new ArrayBlockingQueue<>(maxTasksCount);
        allDone = new AtomicInteger();
        servers = new Semaphore(serversCount);
        Manager manager = new Manager(tasks);
        FutureTask<Integer> managerFuture = new FutureTask<>(manager);
        ExecutorService managerService = Executors.newSingleThreadExecutor();
        ArrayList<Worker> workers = getWorkers();
        ExecutorService computerService = Executors.newFixedThreadPool(workerCount / 2);
        List<Future<?>> workerFutures = new ArrayList<>();
        for(Callable<?> worker: workers){
            workerFutures.add(computerService.submit(worker));
        }
        managerService.execute(managerFuture);
        while(true){
            if(managerFuture.isDone()){
                managerService.shutdownNow();
                for(Future<?> workerFuture: workerFutures){
                    workerFuture.cancel(true);
                }
                computerService.shutdownNow();
                System.exit(0);
            }
        }


    }

    private static ArrayList<Worker> getWorkers() {
        ArrayList<Worker> workers = new ArrayList<>();
        for (int workerNumber = 0; workerNumber < workerCount; workerNumber++) {
            String workerName = "Worker" + workerNumber;
            Worker worker = new Worker(workerName, tasks, servers, allDone, countTasksForDay);
            workers.add(worker);
        }
        return workers;
    }
}
