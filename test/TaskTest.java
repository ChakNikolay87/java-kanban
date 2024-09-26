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

public class TaskTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void taskShouldBeEqualsToTaskWithSameId() {
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        inMemoryTaskManager.addTask(task1);
        Task task2 = new Task("Переезд",
                "Собрать вещи",
                task1.getId(),
                task1.getStatus(),
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        Assertions.assertEquals(task1, task2, "Задачи не равны");
        task1.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(task1.getStatus(), task2.getStatus(), "Статусы равны");
        Assertions.assertNotEquals(task1, task2, "Задачи равны");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void epicShouldBeEqualsToEpicWithSameId() {
        Epic epic1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(epic1);
        Epic epic2 = new Epic("Переезд", "Собрать вещи", epic1.getStatus(), epic1.getId());
        Assertions.assertEquals(epic1, epic2, "Эпики не равны");
        epic2.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(epic1.getStatus(), epic2.getStatus(), "Статусы равны");
        Assertions.assertNotEquals(epic1, epic2, "Эпики равны");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void subtaskShouldBeEqualsToSubtaskWithSameId() {
        Epic epic1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(epic1);
        Subtask subtusk2 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        inMemoryTaskManager.addSubtask(subtusk2);
        Subtask subtusk3 = new Subtask(subtusk2.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                subtusk2.getStatus(),
                subtusk2.getSubtasksEpicId(),
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Assertions.assertEquals(subtusk2, subtusk3, "Задачи не равны");
        subtusk3.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(subtusk2.getStatus(), subtusk3.getStatus(), "Статусы равны");
        Assertions.assertNotEquals(subtusk2, subtusk3, "Подзадачи равны");
        Epic epic4 = new Epic("Переезд", "Собрать вещи", epic1.getId(), epic1.getStatus(), epic1.getDuration(), epic1.getStartTime());
        Assertions.assertEquals(epic1, epic4, "Эпики не равны");
        inMemoryTaskManager.updateSubtask(subtusk3);
        epic4.setStatus(Status.INPROGRESS);
        Assertions.assertEquals(epic1, epic4, "Эпики не равны");
        inMemoryTaskManager.setNextId(1);
    }
}
