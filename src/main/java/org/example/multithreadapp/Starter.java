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

    private static final Manager MANAGER = new Manager(tasks);
    private static final FutureTask<Integer> managerFuture = new FutureTask<>(MANAGER);
    private static final ExecutorService managerService = Executors.newSingleThreadExecutor();

    private static final ArrayList<Developer> DEVELOPERS = new ArrayList<>();
    private static final List<Future<?>> workerFutures = new ArrayList<>();

    private static final ReportManager REPORT_MANAGER = new ReportManager(tasks);
    private static final FutureTask<Integer> leaderFuture = new FutureTask<>(REPORT_MANAGER);
    private static final ScheduledExecutorService leaderService = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) {
        fillWorkers();
        fillWorkerFutures();

        leaderService.scheduleWithFixedDelay(leaderFuture, workConfiguration.getStartInterval(), workConfiguration.getInterval(), TimeUnit.SECONDS);

        managerService.execute(managerFuture);
        while(true){
            if(managerFuture.isDone()){
                managerService.shutdownNow();
                for(Future<?> workerFuture: workerFutures){
                    workerFuture.cancel(true);
                }
                computerService.shutdownNow();
                leaderService.shutdownNow();
                System.exit(0);
            }
        }


    }

    private static void fillWorkerFutures() {
        for(Callable<?> worker: DEVELOPERS){
            workerFutures.add(computerService.submit(worker));
        }
    }

    private static void fillWorkers() {
        for (int workerNumber = 0; workerNumber < workConfiguration.getWorkerCount(); workerNumber++) {
            String workerName = "Worker" + workerNumber;
            Developer developer = new Developer(workerName, tasks, servers, allDone, workConfiguration.getCountTasksForDay());
            DEVELOPERS.add(developer);
        }
        LOG.debug("List of workers filled");
    }
}
