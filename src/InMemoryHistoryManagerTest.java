import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
    }
    @Test
    void add() {
        Task task = new Task(1, "Task 1", "Description 1");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void getHistory() {
        Task task1 = new Task(1, "Task 1", "Description 1");
        Task task2 = new Task(2, "Task 2", "Description 2");
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1,history.get(0));
        assertEquals(task2,history.get(1));
    }
}