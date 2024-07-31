import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.subtasks = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {

        return subtasks;
    }

    public void addSubtask(Subtask subtask) {

        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {

        subtasks.remove(subtask);
    }

    @Override
    public TaskStatus getStatus() {
        if (subtasks.isEmpty()) {
            return TaskStatus.NEW;
        }

        boolean allDone = true;
        boolean allNew = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allNew = false;
            }
        }

        if (allDone) {
            return TaskStatus.DONE;
        }
        if (allNew) {
            return TaskStatus.NEW;
        }
        return TaskStatus.IN_PROGRESS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}
