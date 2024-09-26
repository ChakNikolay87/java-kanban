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
import java.util.Optional;

public class InMemoryTaskManagerTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void inMemoryTaskManagerShouldPutDifferentTypeOfTasks() {
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
        Task task2 = inMemoryTaskManager.addTask(task1);
        Optional task2Optional = inMemoryTaskManager.getTask(1);
        Task task3 = (Task) task2Optional.get();
        Epic epic2 = inMemoryTaskManager.addEpic(epic1);
        Optional epic2Optional = inMemoryTaskManager.getEpic(2);
        Epic epic3 = (Epic) epic2Optional.get();
        Subtask subtask2 = inMemoryTaskManager.addSubtask(subtask1);
        Optional subtask3Optional = inMemoryTaskManager.getSubtask(3);
        Subtask subtask3 = (Subtask) subtask3Optional.get();
        Assertions.assertEquals(task2, task3, "Задачи не равны");
        Assertions.assertEquals(epic2, epic3, "Задачи не равны");
        Assertions.assertEquals(subtask2, subtask3, "Задачи не равны");
        inMemoryTaskManager.deleteEpic(2);
        Optional epic4Optional = inMemoryTaskManager.getEpic(2);
        Optional subtask4Optional = inMemoryTaskManager.getSubtask(3);
        Assertions.assertTrue(epic4Optional.isEmpty(), "Эпик не удален");
        Assertions.assertTrue(subtask4Optional.isEmpty(), "Подзадача не удалена вместе с эпиком");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void subtaskShouldUpdateEpicStatus() {
        Epic epic1 = new Epic("Переезд", "Собрать вещи");
        inMemoryTaskManager.addEpic(epic1);
        Subtask subtask2 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        inMemoryTaskManager.addSubtask(subtask2);
        Subtask subtask3 = new Subtask("Опоры",
                "Начертить опоры",
                1,
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 28, 8, 0));
        inMemoryTaskManager.addSubtask(subtask3);
        Epic epic4 = new Epic("Переезд", "Собрать вещи", epic1.getId(), epic1.getStatus(), epic1.getDuration(), epic1.getStartTime());
        Assertions.assertEquals(epic1, epic4, "Эпики не равны");
        Subtask subtask5 = new Subtask(subtask2.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.INPROGRESS,
                subtask2.getSubtasksEpicId(),
                subtask2.getDuration(),
                subtask2.getStartTime());
        inMemoryTaskManager.updateSubtask(subtask5);
        epic4.setStatus(Status.INPROGRESS);
        Assertions.assertEquals(epic1, epic4, "Эпики не равны");
        Subtask subtusk6 = new Subtask(subtask2.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.DONE,
                subtask2.getSubtasksEpicId(),
                subtask2.getDuration(),
                subtask2.getStartTime());
        inMemoryTaskManager.updateSubtask(subtusk6);
        Assertions.assertEquals(epic1, epic4, "Эпики не равны");
        Subtask subtask7 = new Subtask(subtask3.getId(),
                "Пролетное строение",
                "Начертить пролетное строение",
                Status.DONE,
                subtask3.getSubtasksEpicId(),
                subtask3.getDuration(),
                subtask3.getStartTime());
        inMemoryTaskManager.updateSubtask(subtask7);
        epic4.setStatus(Status.DONE);
        Assertions.assertEquals(epic1, epic4, "После установки статусов Status.DONE всем Subtask " +
                "epic1 не равен epic4");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void addTaskShouldThrowException() {
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        inMemoryTaskManager.addEpic(epic1);
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        Task task2 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 11, 19));
        Task task3 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(30),
                LocalDateTime.of(2024, 9, 23, 10, 21));
        Task task4 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 9, 21));
        Subtask subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Subtask subtask2 = new Subtask("Опоры",
                "Начертить опоры",
                1,
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 15, 8, 0));
        inMemoryTaskManager.addTask(task1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addTask(task2);
        }, "Исключение не выброшено");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addTask(task3);
        }, "Исключение не выброшено");
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addTask(task4);
        }, "Исключение не выброшено");
        inMemoryTaskManager.addSubtask(subtask1);
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addSubtask(subtask2);
        }, "Исключение не выброшено");
        inMemoryTaskManager.setNextId(1);
    }

    @Test
    public void addTaskShouldntThrowException() {
        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        inMemoryTaskManager.addEpic(epic1);
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        Task task2 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 8, 19));
        Subtask subtask1 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                1,
                Duration.ofDays(1),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Subtask subtask2 = new Subtask("Опоры",
                "Начертить опоры",
                1,
                Duration.ofDays(2),
                LocalDateTime.of(2024, 10, 16, 8, 0));
        inMemoryTaskManager.addTask(task1);
        Assertions.assertDoesNotThrow(() -> {
            inMemoryTaskManager.addTask(task2);
        }, "Исключение выброшено");
        inMemoryTaskManager.addSubtask(subtask1);
        Assertions.assertDoesNotThrow(() -> {
            inMemoryTaskManager.addSubtask(subtask2);
        }, "Исключение выброшено");
        inMemoryTaskManager.setNextId(1);
    }
}
