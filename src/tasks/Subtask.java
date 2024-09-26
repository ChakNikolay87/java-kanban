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

    public static Subtask fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType taskType = TaskType.valueOf(fields[1]);
        String name = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];
        int epicId = Integer.parseInt(fields[5]);
        long durationMinutes = Long.parseLong(fields[6]);
        Duration duration = Duration.ofMinutes(durationMinutes);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime startTime = LocalDateTime.parse(fields[7], formatter);

        if (taskType == TaskType.SUBTASK) {
            return new Subtask(id, name, description, status, epicId, duration, startTime);
        }
        throw new IllegalArgumentException(String.format("Неподдерживаемый тип задачи: %s", taskType));
    }
}
