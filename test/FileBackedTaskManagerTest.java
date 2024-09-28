import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager fileBackedTaskManager;
    private File file;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    @BeforeEach
    public void setUp() throws IOException {
        file = File.createTempFile("test", ".csv");
        fileBackedTaskManager = new FileBackedTaskManager(file);
        fileBackedTaskManager.setNextId(1);
    }

    @Test
    public void shouldSaveTasksToFile() throws IOException {
        Task task = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));

        Epic epic = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        fileBackedTaskManager.addEpic(epic);

        Subtask subtask = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                epic.getId(),
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));

        fileBackedTaskManager.addTask(task);
        fileBackedTaskManager.addSubtask(subtask);
        fileBackedTaskManager.save();

        String savedData = Files.readString(file.toPath());

        String expectedData = "Список сохраненных задач:\n" +
                String.format("%d,TASK,Переезд,NEW,Собрать вещи,60,%s\n",
                        task.getId(), task.getStartTime().format(formatter)) +
                String.format("%d,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга,20160,%s\n",
                        epic.getId(), epic.getStartTime().format(formatter)) +  // Добавляем duration и startTime
                String.format("%d,SUBTASK,Пролетное строение,NEW,Начертить пролетное строение,%d,20160,%s\n",
                        subtask.getId(), epic.getId(), subtask.getStartTime().format(formatter));

        System.out.println("Expected:\n" + expectedData);
        System.out.println("Actual:\n" + savedData);

        assertEquals(expectedData, savedData);
    }


    @Test
    public void shouldLoadTasksFromFile() throws IOException {
        String fileContent = "Список сохраненных задач:\n" +
                "1,TASK,Переезд,NEW,Собрать вещи,60,10:20 23.09.2024\n" +
                "2,EPIC,Чертежи моста,NEW,Сделать проект моста через реку Волга\n" +
                "3,SUBTASK,Пролетное строение,NEW,Начертить пролетное строение,2,20160,08:00 13.10.2024\n";
        Files.writeString(file.toPath(), fileContent);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        Map<Integer, Task> tasks = loadedManager.getTasks();
        Map<Integer, Epic> epics = loadedManager.getEpics();
        Map<Integer, Subtask> subtasks = loadedManager.getSubtasks();

        assertEquals(1, tasks.size());
        assertEquals(1, epics.size());
        assertEquals(1, subtasks.size());

        Task task = tasks.get(1);
        assertEquals("Переезд", task.getName());
        assertEquals(Status.NEW, task.getStatus());
        assertEquals(Duration.ofMinutes(60), task.getDuration());
        assertEquals(LocalDateTime.of(2024, 9, 23, 10, 20), task.getStartTime());

        Epic epic = epics.get(2);
        assertEquals("Чертежи моста", epic.getName());
        assertEquals(Status.NEW, epic.getStatus());

        Subtask subtask = subtasks.get(3);
        assertEquals("Пролетное строение", subtask.getName());
        assertEquals(Status.NEW, subtask.getStatus());
        assertEquals(2, subtask.getSubtasksEpicId());
        assertEquals(Duration.ofMinutes(20160), subtask.getDuration());
        assertEquals(LocalDateTime.of(2024, 10, 13, 8, 0), subtask.getStartTime());
    }
}
