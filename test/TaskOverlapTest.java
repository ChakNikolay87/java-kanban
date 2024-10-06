import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class TaskOverlapTest {
    TaskManager inMemoryTaskManager = Managers.getDefault();

    @Test
    public void shouldThrowExceptionWhenTasksOverlap() {
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        inMemoryTaskManager.addTask(task1);

        Task overlappingTask = new Task("Переезд 2",
                "Другая задача",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 50));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addTask(overlappingTask);
        }, "Пересекающаяся задача должна вызывать исключение");
    }

    @Test
    public void shouldNotThrowExceptionWhenTasksDoNotOverlap() {
        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        inMemoryTaskManager.addTask(task1);

        Task nonOverlappingTask = new Task("Переезд 2",
                "Другая задача",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 11, 30));

        Assertions.assertDoesNotThrow(() -> {
            inMemoryTaskManager.addTask(nonOverlappingTask);
        }, "Задачи без пересечений должны добавляться успешно");
    }

    @Test
    public void shouldThrowExceptionWhenSubtasksOverlapWithinEpic() {
        Epic epic = new Epic("Эпик проекта", "Описание эпика");
        inMemoryTaskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1",
                "Начертить",
                epic.getId(),
                Duration.ofMinutes(120),
                LocalDateTime.of(2024, 9, 24, 10, 0));
        inMemoryTaskManager.addSubtask(subtask1);

        Subtask overlappingSubtask = new Subtask("Подзадача 2",
                "Расчеты",
                epic.getId(),
                Duration.ofMinutes(90),
                LocalDateTime.of(2024, 9, 24, 11, 0));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addSubtask(overlappingSubtask);
        }, "Пересекающаяся подзадача должна вызывать исключение");
    }

    @Test
    public void shouldNotThrowExceptionWhenSubtasksDoNotOverlapWithinEpic() {
        Epic epic = new Epic("Эпик проекта", "Описание эпика");
        inMemoryTaskManager.addEpic(epic);

        Subtask subtask1 = new Subtask("Подзадача 1",
                "Начертить",
                epic.getId(),
                Duration.ofMinutes(120),
                LocalDateTime.of(2024, 9, 24, 10, 0));
        inMemoryTaskManager.addSubtask(subtask1);

        Subtask nonOverlappingSubtask = new Subtask("Подзадача 2",
                "Расчеты",
                epic.getId(),
                Duration.ofMinutes(90),
                LocalDateTime.of(2024, 9, 24, 12, 30));

        Assertions.assertDoesNotThrow(() -> {
            inMemoryTaskManager.addSubtask(nonOverlappingSubtask);
        }, "Подзадачи без пересечений должны добавляться успешно");
    }

    @Test
    public void shouldThrowExceptionWhenSubtaskOverlapsWithTask() {
        Task task = new Task("Задача",
                "Обычная задача",
                Duration.ofMinutes(90),
                LocalDateTime.of(2024, 9, 25, 9, 0));
        inMemoryTaskManager.addTask(task);

        Epic epic = new Epic("Эпик", "Эпик задачи");
        inMemoryTaskManager.addEpic(epic);

        Subtask overlappingSubtask = new Subtask("Подзадача 1",
                "Описание подзадачи",
                epic.getId(),
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 25, 9, 30));

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            inMemoryTaskManager.addSubtask(overlappingSubtask);
        }, "Подзадача, пересекающаяся с обычной задачей, должна вызывать исключение");
    }
}
