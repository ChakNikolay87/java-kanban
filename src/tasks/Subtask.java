package tasks;

import managers.TaskType;

import java.util.Objects;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(int id, String name, String description, int epicId, TaskStatus status) {
        super(id, name, description, status);
        this.epicId = epicId;
    }


    public int getEpicId() {
        return epicId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }

    @Override
    public String toString() {
        return String.format("%s,%d", super.toString(), epicId);
    }

    @Override
    public String taskToString() {
        return String.format("%d,%s,%s,%s,%s,%d", getId(), TaskType.SUBTASK.name(), getName(), getStatus(), getDescription(), getEpicId());
    }

    public static Subtask fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType taskType = TaskType.valueOf(fields[1]);
        String name = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        int epicId = Integer.parseInt(fields[5]);

        if (taskType == TaskType.SUBTASK) {
            return new Subtask(id, name, description, epicId, status);
        }
        throw new IllegalArgumentException("Неподдерживаемый тип задачи: %s" + taskType);
    }

    @Override
    public String type() {
        return TaskType.SUBTASK.name();
    }
}
