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
    }

    @Test
    public void subtaskShouldBeEqualsToSubtaskWithSameId() {
        Epic epic1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(epic1);
        Subtask subtask2 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                epic1.getId(),
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        inMemoryTaskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask(subtask2.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                subtask2.getStatus(),
                subtask2.getSubtasksEpicId(),
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Assertions.assertEquals(subtask2, subtask3, "Задачи не равны");
        subtask3.setStatus(Status.INPROGRESS);
        Assertions.assertNotEquals(subtask2.getStatus(), subtask3.getStatus(), "Статусы равны");
        Assertions.assertNotEquals(subtask2, subtask3, "Подзадачи равны");

        Epic epic4 = new Epic("Переезд", "Собрать вещи", epic1.getId(), epic1.getStatus(), epic1.getDuration(), epic1.getStartTime());
        Assertions.assertEquals(epic1, epic4, "Эпики не равны");
        inMemoryTaskManager.updateSubtask(subtask3);
        epic4.setStatus(Status.INPROGRESS);
        Assertions.assertEquals(epic1, epic4, "Эпики не равны");
    }
}
