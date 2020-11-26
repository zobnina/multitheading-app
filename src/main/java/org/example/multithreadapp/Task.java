package org.example.multithreadapp;

public class Task {
    private final String name;
    private final int complexity;

    public Task(String name, int complexity) {
        this.name = name;
        this.complexity = complexity;
    }

    String getName() {
        return name;
    }

    int getComplexity() {
        return complexity;
    }
}