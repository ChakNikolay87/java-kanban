import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import status.Status;

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
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Subtask subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                2,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        String message = "Задача в листе не равна вызванной ранее";
        inMemoryTaskManager.addTask(task1);
        inMemoryTaskManager.getTask(1);
        inMemoryTaskManager.addEpic(epic1);
        inMemoryTaskManager.getEpic(2);
        inMemoryTaskManager.addSubtask(subtask1);
        inMemoryTaskManager.getSubtask(3);
        List<Task> listTest = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(task1, listTest.get(0), message);
        Assertions.assertEquals(epic1, listTest.get(1), message);
        Assertions.assertEquals(subtask1, listTest.get(2), message);
        task1.setStatus(Status.INPROGRESS);
        inMemoryTaskManager.getTask(1);
        List<Task> listTest2 = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest2.get(0), message);
        Assertions.assertEquals(subtask1, listTest2.get(1), message);
        Assertions.assertEquals(task1, listTest2.get(2), message);
        Task task2 = new Task("Стрижка",
                "Сходить в барбершоп",
                Duration.ofHours(3),
                LocalDateTime.of(2024, 9, 24, 17, 0));
        inMemoryTaskManager.addTask(task2);
        inMemoryTaskManager.getTask(4);
        List<Task> listTest3 = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest3.get(0), message);
        Assertions.assertEquals(subtask1, listTest3.get(1), message);
        Assertions.assertEquals(task1, listTest3.get(2), message);
        Assertions.assertEquals(task2, listTest3.get(3), message);
        inMemoryTaskManager.deleteTask(1);
        List<Task> listTest4 = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest4.get(0), message);
        Assertions.assertEquals(subtask1, listTest4.get(1), message);
        Assertions.assertEquals(task2, listTest4.get(2), message);
        inMemoryTaskManager.deleteSubtask(3);
        List<Task> listTest5 = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest5.get(0), message);
        Assertions.assertEquals(task2, listTest5.get(1), message);
        Subtask subtask2 = new Subtask("Опоры",
                "Начертить опоры",
                2,
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 28, 8, 0));
        inMemoryTaskManager.addSubtask(subtask2);
        inMemoryTaskManager.getSubtask(5);
        List<Task> listTest6 = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(epic1, listTest6.get(0), message);
        Assertions.assertEquals(task2, listTest6.get(1), message);
        Assertions.assertEquals(subtask2, listTest6.get(2), message);
        inMemoryTaskManager.deleteEpic(2);
        List<Task> listTest7 = inMemoryTaskManager.getHistory();
        Assertions.assertEquals(task2, listTest7.get(0), message);
        inMemoryTaskManager.setNextId(1);
    }
}
