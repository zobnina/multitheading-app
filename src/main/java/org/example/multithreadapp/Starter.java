package org.example.multithreadapp;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Starter {
    static Logger logger = LogManager.getLogger(Starter.class);

    public static void main(String[] args) {
logger.error("hi");
logger.info("hi");
    }
}
