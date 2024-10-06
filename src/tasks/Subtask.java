package tasks;

import managers.TaskType;
import status.Status;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {
    private final int subtasksEpicId;

    public Subtask(String name, String description, int subtasksEpicId, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.subtasksEpicId = subtasksEpicId;
    }

    public Subtask(int id, String name, String description, Status status, int subtasksEpicId, Duration duration,
                   LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.subtasksEpicId = subtasksEpicId;
    }

    public int getSubtasksEpicId() {
        return subtasksEpicId;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return String.format("%d,%s,%s,%s,%s,%d,%d,%s", getId(), TaskType.SUBTASK, getName(), getStatus(),
                getDescription(), subtasksEpicId, getDuration().toMinutes(), getStartTime().format(formatter));
    }
}
