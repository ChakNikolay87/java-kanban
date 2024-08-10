package history;

import history.HistoryManager;
import tasks.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private static final int HISTORY_LIMIT = 10;
    private final List<Task> history = new LinkedList<>();

    @Override
    public void add(Task task) {
        Task taskCopy = new Task(task.getId(), task.getName(), task.getDescription());
        if (history.size() == HISTORY_LIMIT) {
            history.removeFirst();
        }
        history.add(taskCopy);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }

}
