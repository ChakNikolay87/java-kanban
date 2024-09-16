package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        return subtask;
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(Path.of(file.getPath()));
            Map<Integer, Epic> epicMap = new HashMap<>();
            Map<Integer, Subtask> subtaskMap = new HashMap<>();

            for (String line : lines) {
                if (!line.startsWith("id")) {
                    Task task = fromString(line);
                    if (task instanceof Epic) {
                        manager.createEpic((Epic) task);
                        epicMap.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        subtaskMap.put(task.getId(), (Subtask) task);
                    } else {
                        manager.createTask(task);
                    }
                }
            }

            for (Subtask subtask : subtaskMap.values()) {
                Epic epic = epicMap.get(subtask.getEpicId());
                if (epic != null) {
                    manager.createSubtask(subtask);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load from file", e);
        }
        return manager;
    }


    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        switch (TaskType.valueOf(type)) {
            case TASK:
                return new Task(id, name, description, status);
            case EPIC:
                return new Epic(id, name, description, status);
            case SUBTASK:
                int epicId = Integer.parseInt(fields[5]);
                return new Subtask(id, name, description, epicId, status);
            default:
                throw new IllegalArgumentException("Unknown task type: " + type);
        }
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
