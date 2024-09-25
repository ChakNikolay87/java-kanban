package tasks;

import manager.TaskType;
import status.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Objects;

public class Epic extends Task {
    private HashMap<Integer, Subtask> epicSubtusks;

    public Epic(String name, String description) {
        super(name, description);
        this.epicSubtusks = new HashMap<>();

    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.epicSubtusks = new HashMap<>();
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        this.epicSubtusks = new HashMap<>();
    }

    public Epic(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);

        this.epicSubtusks = new HashMap<>();
    }

    public HashMap<Integer, Subtask> getEpicSubtusks() {
        return epicSubtusks;
    }

    public void setEpicSubtusks(HashMap<Integer, Subtask> epicSubtusks) {
        this.epicSubtusks = epicSubtusks;
    }

    public void put(Subtask subtask) {
        epicSubtusks.put(subtask.getId(), subtask);
        super.setStatus(isSubtasksDone());
        updateEpicTime();
    }

    public void clear() {
        epicSubtusks.clear();
    }

    public Status isSubtasksDone() {
        if (epicSubtusks.values().stream().anyMatch(subtask -> subtask.getStatus() == Status.INPROGRESS)) {
            return Status.INPROGRESS;
        }
        if (epicSubtusks.values().stream().allMatch(subtask -> subtask.getStatus() == Status.DONE)) {
            return Status.DONE;
        }
        if (epicSubtusks.values().stream().allMatch(subtask -> subtask.getStatus() == Status.NEW)) {
            return Status.NEW;
        }
        return Status.INPROGRESS;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return String.format("%d,%s,%s,%s,%s,%d,%s",
                getId(),
                TaskType.EPIC,
                getName(),
                getStatus(),
                getDescription(),
                getDuration() != null ? getDuration().toMinutes() : 0,
                getStartTime() != null ? getStartTime().format(formatter) : "null");
    }

    public static Epic fromString(String value) {
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

        if (taskType == TaskType.EPIC) {
            Epic epic = new Epic(name, description, id, status, duration, startTime);
            epic.setDuration(duration); // Используем метод для установки duration
            epic.setStartTime(startTime);
            return epic;
        }
        throw new IllegalArgumentException(String.format("Неподдерживаемый тип задачи: %s", taskType));
    }

    public void updateEpicTime() {
        if (getEpicSubtusks().isEmpty()) {
            setStartTime(null);
            setDuration(Duration.ZERO);
        } else {
            LocalDateTime startTime = getEpicSubtusks().values().stream()
                    .map(Subtask::getStartTime)
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            LocalDateTime endTime = getEpicSubtusks().values().stream()
                    .map(Subtask::getEndTime)
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            setStartTime(startTime);

            if (startTime != null && endTime != null) {
                setDuration(Duration.between(startTime, endTime));
            } else {
                setDuration(Duration.ZERO);
            }
        }
    }
}
