package com.example.androidapp;
import java.util.Calendar;

public class Task {

    private String id;
    private String taskName;
    private String taskDescription;
    private String priority;
    private Calendar dueDate;
    private boolean isCompleted;

    public Task() {}

    public Task(String taskName, String taskDescription, String priority, Calendar dueDate) {
        this.id = id;
        this.taskName = taskName;
        this.taskDescription = taskDescription;
        this.priority = priority;
        this.dueDate = dueDate;
        this.isCompleted = false;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getTaskDescription() {
        return taskDescription;
    }

    public String getPriority() {
        return priority;
    }

    public Calendar getDueDate() {
        return dueDate;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}