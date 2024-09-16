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
    public String type() {
        return TaskType.SUBTASK.name();
    }
}
