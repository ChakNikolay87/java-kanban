import history.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTaskToHistoryShouldAddTaskToHistory() {
        Task task = new Task(1, "Task Task", "Task Description", TaskStatus.NEW);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
        assertEquals(TaskStatus.NEW, history.get(0).getStatus());
    }

    @Test
    void addTaskToHistoryShouldNotDuplicateTasks() {
        Task task = new Task(1, "Task Task", "Task Description", TaskStatus.NEW);

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
        assertEquals(TaskStatus.NEW, history.get(0).getStatus());
    }

    @Test
    void removeTaskFromHistoryShouldRemoveTaskFromHistory() {
        Task task1 = new Task(1, "Task Task", "Task Description 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task Task", "Task Description 2", TaskStatus.IN_PROGRESS);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(TaskStatus.IN_PROGRESS, history.get(0).getStatus());
    }

    @Test
    void removeTaskFromHistoryShouldNotRemoveNonExistentTask() {
        Task task = new Task(1, "Task Task", "Task Description", TaskStatus.NEW);

        historyManager.add(task);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
        assertEquals(TaskStatus.NEW, history.get(0).getStatus());
    }

    @Test
    void getHistory_ShouldReturnEmptyList_WhenNoTasksAdded() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void addMultipleTasksToHistoryShouldMaintainCorrectOrder() {
        Task task1 = new Task(1, "Test Task 1", "Task Description 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Test Task 2", "Task Description 2", TaskStatus.IN_PROGRESS);
        Task task3 = new Task(3, "Test Task 3", "Task Description 3", TaskStatus.DONE);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(TaskStatus.NEW, history.get(0).getStatus());
        assertEquals(task2, history.get(1));
        assertEquals(TaskStatus.IN_PROGRESS, history.get(1).getStatus());
        assertEquals(task3, history.get(2));
        assertEquals(TaskStatus.DONE, history.get(2).getStatus());
    }

    @Test
    void addDuplicateTaskShouldReplaceEntry() {
        Task task1 = new Task(1, "Test Task 1", "Task Description 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Test Task 2", "Task Description 2", TaskStatus.IN_PROGRESS);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(TaskStatus.IN_PROGRESS, history.get(0).getStatus());
        assertEquals(task1, history.get(1));
        assertEquals(TaskStatus.NEW, history.get(1).getStatus());
    }

    @Test
    void removeAllTasksShouldReturnEmptyHistory() {
        Task task1 = new Task(1, "Test Task 1", "Task Description 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Test Task 2", "Task Description 2", TaskStatus.IN_PROGRESS);

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

}
