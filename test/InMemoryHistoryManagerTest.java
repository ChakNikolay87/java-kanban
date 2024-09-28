import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class InMemoryHistoryManagerTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void historyManagerShouldPutCurrentTasks() {
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        inMemoryTaskManager.addTask(task1);

        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        inMemoryTaskManager.addEpic(epic1);

        Subtask subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                epic1.getId(),
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        inMemoryTaskManager.addSubtask(subtask1);

        inMemoryTaskManager.getEpic(epic1.getId());
        inMemoryTaskManager.getSubtaskById(subtask1.getId());
        inMemoryTaskManager.getTask(task1.getId());

        List<Task> history = inMemoryTaskManager.getHistory();
        String message = "Task in history does not match the retrieved task";
        Assertions.assertEquals(3, history.size(), "History should contain 3 elements.");
        Assertions.assertEquals(epic1, history.get(0), "First element should be the epic.");
        Assertions.assertEquals(subtask1, history.get(1), "Second element should be the subtask.");
        Assertions.assertEquals(task1, history.get(2), "Third element should be the task.");

        Task task2 = new Task("Стрижка",
                "Сходить в барбершоп",
                Duration.ofHours(3),
                LocalDateTime.of(2024, 9, 24, 17, 0));
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.getTask(task2.getId());

        List<Task> updatedHistory = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(4, updatedHistory.size(), "History should contain 4 elements.");
        Assertions.assertEquals(epic1, updatedHistory.get(0), "First element should still be the epic.");
        Assertions.assertEquals(subtask1, updatedHistory.get(1), "Second element should still be the subtask.");
        Assertions.assertEquals(task1, updatedHistory.get(2), "Third element should be the initial task.");
        Assertions.assertEquals(task2, updatedHistory.get(3), "Fourth element should be the new task.");

        inMemoryTaskManager.deleteTask(task1.getId());
        List<Task> historyAfterDeletion = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(3, historyAfterDeletion.size(), "History should contain 3 elements after task deletion.");
        Assertions.assertEquals(epic1, historyAfterDeletion.get(0), "First element should still be the epic.");
        Assertions.assertEquals(subtask1, historyAfterDeletion.get(1), "Second element should still be the subtask.");
        Assertions.assertEquals(task2, historyAfterDeletion.get(2), "Third element should be the remaining task.");

        inMemoryTaskManager.deleteSubtask(subtask1.getId());
        List<Task> historyAfterSubtaskDeletion = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(2, historyAfterSubtaskDeletion.size(), "History should contain 2 elements after subtask deletion.");
        Assertions.assertEquals(epic1, historyAfterSubtaskDeletion.get(0), "First element should still be the epic.");
        Assertions.assertEquals(task2, historyAfterSubtaskDeletion.get(1), "Second element should be the remaining task.");

        Subtask subtask2 = new Subtask("Опоры",
                "Начертить опоры",
                epic1.getId(),
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 28, 8, 0));
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.getSubtaskById(subtask2.getId());

        List<Task> historyWithNewSubtask = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(3, historyWithNewSubtask.size(), "History should contain 3 elements after adding new subtask.");
        Assertions.assertEquals(epic1, historyWithNewSubtask.get(0), "First element should still be the epic.");
        Assertions.assertEquals(task2, historyWithNewSubtask.get(1), "Second element should still be the task.");
        Assertions.assertEquals(subtask2, historyWithNewSubtask.get(2), "Third element should be the new subtask.");

        inMemoryTaskManager.deleteEpic(epic1.getId());
        List<Task> finalHistory = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(1, finalHistory.size(), "History should contain 1 element after epic deletion.");
        Assertions.assertEquals(task2, finalHistory.get(0), "Only remaining element should be task2.");
    }
}
