package tasks;

import managers.TaskType;
import status.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Epic extends Task {
    private Map<Integer, Subtask> epicSubtasks;

    public Epic(String name, String description) {
        super(name, description);
        this.epicSubtasks = new HashMap<>();
    }

    public Epic(String name, String description, int id) {
        super(name, description, id);
        this.epicSubtasks = new HashMap<>();
    }

    public Epic(String name, String description, Status status, int id) {
        super(name, description, status, id);
        this.epicSubtasks = new HashMap<>();
    }

    public Epic(String name, String description, int id, Status status, Duration duration, LocalDateTime startTime) {
        super(name, description, id, status, duration, startTime);
        this.epicSubtasks = new HashMap<>();
    }

    public void addSubtask(Subtask subtask) {
        epicSubtasks.put(subtask.getId(), subtask);
        updateEpicStatus();
        updateEpicTime();
    }

    public void removeSubtask(int subtaskId) {
        epicSubtasks.remove(subtaskId);
        updateEpicStatus();
        updateEpicTime();
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(epicSubtasks.values());
    }

    public void clear() {
        epicSubtasks.clear();
        updateEpicStatus();
        updateEpicTime();
    }

    public Optional<Subtask> getSubtaskById(int subtaskId) {
        return Optional.ofNullable(epicSubtasks.get(subtaskId));
    }

    public void updateSubtask(Subtask subtask) {
        if (epicSubtasks.containsKey(subtask.getId())) {
            epicSubtasks.replace(subtask.getId(), subtask);
            updateEpicStatus();
            updateEpicTime();
        } else {
            throw new IllegalArgumentException("Subtask with id=%s not found in epic".formatted(subtask.getId()));
        }
    }

    public void updateEpicTime() {
        updateStartTime();
        updateTotalDuration();
    }

    private void updateTotalDuration() {
        Duration totalDuration = epicSubtasks.values().stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);
        setDuration(totalDuration);
    }

    private void updateStartTime() {
        LocalDateTime startTime = epicSubtasks.values().stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo)
                .orElse(null);
        setStartTime(startTime);
    }

    public Status getSubtasksStatus() {
        if (epicSubtasks.values().stream().anyMatch(subtask -> subtask.getStatus() == Status.INPROGRESS)) {
            return Status.INPROGRESS;
        }
        if (epicSubtasks.values().stream().allMatch(subtask -> subtask.getStatus() == Status.DONE)) {
            return Status.DONE;
        }
        if (epicSubtasks.values().stream().allMatch(subtask -> subtask.getStatus() == Status.NEW)) {
            return Status.NEW;
        }
        return Status.INPROGRESS;
    }

    private void updateEpicStatus() {
        setStatus(getSubtasksStatus());
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
}
