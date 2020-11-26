package org.example.multithreadapp;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Starter {
    private static final Logger LOG = LogManager.getLogger(Starter.class);
    private static final WorkConfiguration workConfiguration = WorkConfiguration.getInstance();

    private static final BlockingQueue<Task> tasks = new ArrayBlockingQueue<>(workConfiguration.getMaxTasksCount());
    private static final AtomicInteger allDone = new AtomicInteger();
    private static final Semaphore servers = new Semaphore(workConfiguration.getServersCount());
    private static final ExecutorService computerService = Executors.newFixedThreadPool(workConfiguration.getWorkerCount() / 2);

    private static final Manager manager = new Manager(tasks);
    private static final FutureTask<Integer> managerFuture = new FutureTask<>(manager);
    private static final ExecutorService managerService = Executors.newSingleThreadExecutor();
    private static final ArrayList<Worker> workers = new ArrayList<>();
    private static final List<Future<?>> workerFutures = new ArrayList<>();

    public static void main(String[] args) {
        fillWorkers();
        fillWorkerFutures();

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

    private static void fillWorkerFutures() {
        for(Callable<?> worker: workers){
            workerFutures.add(computerService.submit(worker));
        }
    }

    private static void fillWorkers() {
        for (int workerNumber = 0; workerNumber < workConfiguration.getWorkerCount(); workerNumber++) {
            String workerName = "Worker" + workerNumber;
            Worker worker = new Worker(workerName, tasks, servers, allDone, workConfiguration.getCountTasksForDay());
            workers.add(worker);
        }
        LOG.debug("List of workers filled");
    }
}
