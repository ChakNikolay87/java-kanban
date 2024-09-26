package tasks;

import managers.TaskType;
import status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    private final String name;
    private final String description;
    private int id;
    private Status status;
    private Duration duration;
    private LocalDateTime startTime;
    protected static String format = "HH:mm dd.MM.yyyy";

    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description, Status status, int id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
    }

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
    }

    public Task(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
    }

    public Task(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public static String getFormat() {
        return format;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return String.format("%d,%s,%s,%s,%s,%d,%s", id, TaskType.TASK, name, status, description, duration.toMinutes(),
                startTime.format(formatter));
    }

    public static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType taskType = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        long durationMinutes = Long.parseLong(fields[5]);
        Duration duration = Duration.ofMinutes(durationMinutes);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime startTime = LocalDateTime.parse(fields[6], formatter);
        if (taskType == TaskType.TASK) {
            return new Task(name, description, id, status, duration, startTime);
        }
        throw new IllegalArgumentException(String.format("Неподдерживаемый тип задачи: %s", taskType));
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id && Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status && Objects.equals(duration, task.duration)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, id, status, duration, startTime);
    }
}
