package tasks;

import managers.TaskType;

import java.util.Objects;

public class Task {
    private int id;
    private String name;
    private String description;
    private TaskStatus status;

    public Task(int id, String name, String description, TaskStatus status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return String.format("%d,%s,%s,%s,%s", id, TaskType.TASK, name, status, description);
    }

    public String taskToString() {
        return String.format("%d,%s,%s,%s,%s", getId(), TaskType.TASK.name(), getName(), getStatus(), getDescription());
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType taskType = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];

        if (taskType == TaskType.TASK) {
            return new Task(id, name, description, status);
        }
        throw new IllegalArgumentException("Неподдерживаемый тип задачи: " + taskType);
    }

    public String type() {
        return TaskType.TASK.name();
    }
}
