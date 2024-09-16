import managers.FileBackedTaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            File tempFile = File.createTempFile("tasks", ".csv");
            tempFile.deleteOnExit();
            FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
            Task task1 = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
            Task task2 = new Task(1, "Task 2", "Description 2", TaskStatus.IN_PROGRESS);
            manager.createTask(task1);
            manager.createTask(task2);
           
            Epic epic1 = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
            Epic epic2 = new Epic(1, "Epic 2", "Description 2", TaskStatus.IN_PROGRESS);
            manager.createEpic(epic1);
            manager.createEpic(epic2);
           
            Subtask subtask1 = new Subtask(0, "Subtask 1", "Description 1", epic1.getId(), TaskStatus.DONE);
            Subtask subtask2 = new Subtask(1, "Subtask 2", "Description 2", epic1.getId(), TaskStatus.IN_PROGRESS);
            Subtask subtask3 = new Subtask(2, "Subtask 3", "Description 3", epic2.getId(), TaskStatus.NEW);
            manager.createSubtask(subtask1);
            manager.createSubtask(subtask2);
            manager.createSubtask(subtask3);
            
            FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
            System.out.println("Loaded tasks: " + loadedManager.getTasks());
            System.out.println("Loaded epics: " + loadedManager.getEpics());
            System.out.println("Loaded subtasks: " + loadedManager.getSubtasks());
        } catch (IOException e) {
            System.err.println("An error occurred while handling the file: " + e.getMessage());
        }
    }
}
