package org.example.multithreadapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.util.Properties;

public class WorkConfiguration {
    private static volatile WorkConfiguration instance;

    private static final Logger LOG = LogManager.getLogger(WorkConfiguration.class);
    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String PROPERTIES_PATH = USER_DIR + "/src/main/resources/work.properties";

    private int startInterval;
    private int interval;
    private int workerCount;
    private int maxTasksCount;
    private int countTasksForDay;
    private int serversCount;

    private WorkConfiguration(){
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

    public static WorkConfiguration getInstance(){
        WorkConfiguration result = instance;
        if (result != null) {
            return result;
        }
        synchronized(WorkConfiguration.class) {
            if (instance == null) {
                instance = new WorkConfiguration();
            }
            return instance;
        }
    }

    public int getStartInterval() {
        return startInterval;
    }

    public int getInterval() {
        return interval;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public int getMaxTasksCount() {
        return maxTasksCount;
    }

    public int getCountTasksForDay() {
        return countTasksForDay;
    }

    public int getServersCount() {
        return serversCount;
    }
}
