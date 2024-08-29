package history;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task);

    void remote(int id);

    List<Task> getHistory();
}
