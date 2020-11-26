package org.example.multithreadapp;

public class Report extends Task {

    public Report(int number, int complexity) {
        super(String.format("Report for the day №%d", number), complexity);
    }
}
