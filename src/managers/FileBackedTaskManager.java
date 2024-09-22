package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    private void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getTasks()) {
                writer.write(taskToString(task) + "\n");
            }
            for (Epic epic : getEpics()) {
                writer.write(taskToString(epic) + "\n");
            }
            for (Subtask subtask : getSubtasks()) {
                writer.write(taskToString(subtask) + "\n");
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Failed to save to file", e);
        }
    }

    private String taskToString(Task task) {
        String epicId = (task instanceof Subtask) ? String.valueOf(((Subtask) task).getEpicId()) : "";
        return String.format("%d,%s,%s,%s,%s,%s", task.getId(), getTaskType(task), task.getName(),
                task.getStatus(), task.getDescription(), epicId);
    }

    private String getTaskType(Task task) {
        if (task instanceof Subtask) {
            return TaskType.SUBTASK.name();
        } else if (task instanceof Epic) {
            return TaskType.EPIC.name();
        } else {
            return TaskType.TASK.name();
        }
    }

    @Override
    public Task addTask(Task task) {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask newSubtask = super.addSubtask(subtask);
        save();
        return newSubtask;
    }


    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        int maxId = 0;

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                String[] fields = line.split(",");
                TaskType taskType = TaskType.valueOf(fields[1]);

                switch (taskType) {
                    case TASK:
                        Task task = Task.fromString(line);
                        manager.addTask(task);
                        maxId = Math.max(maxId, task.getId());
                        break;
                    case SUBTASK:
                        Subtask subtask = Subtask.fromString(line);
                        manager.addSubtask(subtask);
                        maxId = Math.max(maxId, subtask.getId());
                        break;
                    case EPIC:
                        Epic epic = Epic.fromString(line);
                        manager.addEpic(epic);
                        maxId = Math.max(maxId, epic.getId());
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Неизвестный тип задачи: %s", taskType));
                }
            }
            manager.idCounter = maxId + 1;
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Ошибка при загрузке данных из файла: %s", file.getName()), e);
        }
        return manager;
    }



    public static void main(String[] args) throws IOException {

        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();


        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task(0, "Test Task", "Test Description", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic(1, "Test Epic", "Epic Description", TaskStatus.IN_PROGRESS);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Test Subtask", "Subtask Description", epic.getId(), TaskStatus.DONE);
        manager.createSubtask(subtask);


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);


        System.out.println("Loaded tasks: " + loadedManager.getTasks());
        System.out.println("Loaded epics: " + loadedManager.getEpics());
        System.out.println("Loaded subtasks: " + loadedManager.getSubtasks());
    }
}
