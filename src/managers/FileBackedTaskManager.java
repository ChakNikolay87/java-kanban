package managers;

import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() {
        try (Writer writer = new FileWriter(file)) {
            writer.write("Список сохраненных задач:\n");
            for (Task task : tasksMap.values()) {
                writer.write(task.toString() + "\n");
            }
            for (Epic epic : epicsMap.values()) {
                writer.write(epic.toString() + "\n");
            }
            for (Subtask subtask : subtasksMap.values()) {
                writer.write(subtask.toString() + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Ошибка при сохранении данных в файл: %s", file.getName()));
        }
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        TaskType taskType = TaskType.valueOf(fields[1]);

        switch (taskType) {
            case TASK:
                return parseTask(fields);
            case SUBTASK:
                return parseSubtask(fields);
            case EPIC:
                return parseEpic(fields);
            default:
                throw new IllegalArgumentException("Unknown task type: " + taskType);
        }
    }

    private static Task parseTask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        long durationMinutes = Long.parseLong(fields[5]);
        Duration duration = Duration.ofMinutes(durationMinutes);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Task.getFormat());
        LocalDateTime startTime = LocalDateTime.parse(fields[6], formatter);
        return new Task(name, description, id, status, duration, startTime);
    }

    private static Subtask parseSubtask(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        int epicId = Integer.parseInt(fields[5]);
        long durationMinutes = Long.parseLong(fields[6]);
        Duration duration = Duration.ofMinutes(durationMinutes);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Task.getFormat());
        LocalDateTime startTime = LocalDateTime.parse(fields[7], formatter);
        return new Subtask(id, name, description, status, epicId, duration, startTime);
    }

    private static Epic parseEpic(String[] fields) {
        int id = Integer.parseInt(fields[0]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        Epic epic = new Epic(name, description);
        epic.setId(id);
        epic.setStatus(status);
        return epic;
    }


    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                Task task = fromString(line);
                if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else {
                    manager.addTask(task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Error loading data from file: %s", file.getName()));
        }
        return manager;
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
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updatedEpic = super.updateEpic(epic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Subtask updatedSubtask = super.updateSubtask(subtask);
        save();
        return updatedSubtask;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }
}
