package test;

import history.HistoryManager;
import managers.Managers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;

    @BeforeEach
    public void setUp() {

        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addTaskToHistoryShouldAddTaskToHistory() {
        Task task = new Task(1, "Task Task", "Task Description");
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void addTaskToHistoryShouldNotDuplicateTasks() {
        Task task = new Task(1, "Task Task", "Task Description");

        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void removeTaskFromHistoryShouldRemoveTaskFromHistory() {
        Task task1 = new Task(1, "Task Task", "Task Description 1");
        Task task2 = new Task(2, "Task Task", "Task Description 2");

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void removeTaskFromHistoryShouldNotRemoveNonExistentTask() {
        Task task = new Task(1, "Task Task", "Task Description");

        historyManager.add(task);
        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void getHistory_ShouldReturnEmptyList_WhenNoTasksAdded() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

    @Test
    void addMultipleTasksToHistoryShouldMainCorrectOrder() {
        Task task1 = new Task(1, "Test Task 1", "Task Description 1");
        Task task2 = new Task(2, "Test Task 2", "Task Description 2");
        Task task3 = new Task(3, "Test Task 3", "Task Description 3");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }


    @Test
    void addDuplicateTaskShouldReplaceEntry() {
        Task task1 = new Task(1, "Test Task 1", "Task Description 1");
        Task task2 = new Task(2, "Test Task 2", "Task Description 2");

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
        assertEquals(task1, history.get(1));
    }


    @Test
    void removeAllTasksShouldReturnEmptyHistory() {
        Task task1 = new Task(1, "Test Task 1", "Task Description 1");
        Task task2 = new Task(2, "Test Task 2", "Task Description 2");

        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.remove(task1.getId());
        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty());
    }

}
