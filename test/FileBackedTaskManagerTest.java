import managers.FileBackedTaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File file;

    @BeforeEach
    void setUp() throws IOException {
        file = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(file);
    }

    @Test
    void shouldSaveAndLoadTaskCorrectly() {
        Task task = new Task(0, "Test Task", "Test Description", TaskStatus.NEW);
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertEquals(1, loadedManager.getTasks().size());
        Task loadedTask = loadedManager.getTasks().get(0);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
    }

    @Test
    void shouldSaveAndLoadEpicCorrectly() {
        Epic epic = new Epic(0, "Test Epic", "Epic Description", TaskStatus.NEW);
        manager.createEpic(epic);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getEpics().size());
        Epic loadedEpic = loadedManager.getEpics().get(0);
        assertEquals(epic.getName(), loadedEpic.getName());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
        assertEquals(epic.getStatus(), loadedEpic.getStatus());
    }

    @Test
    void shouldSaveAndLoadSubtaskCorrectly() {
        Epic epic = new Epic(0, "Test Epic", "Epic Description", TaskStatus.NEW);
        manager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Test Subtask", "Subtask Description", epic.getId(), TaskStatus.NEW);
        manager.createSubtask(subtask);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(1, loadedManager.getEpics().size());
        Subtask loadedSubtask = loadedManager.getSubtasks().get(0);
        assertEquals(subtask.getName(), loadedSubtask.getName());
        assertEquals(subtask.getDescription(), loadedSubtask.getDescription());
        assertEquals(subtask.getEpicId(), loadedSubtask.getEpicId());
        assertEquals(subtask.getStatus(), loadedSubtask.getStatus());
    }

    @Test
    void shouldDeleteTaskAndSaveChanges() {
        Task task = new Task(0, "Test Task", "Test Description", TaskStatus.NEW);
        manager.createTask(task);

        manager.deleteTaskById(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(0, loadedManager.getTasks().size());
    }

    @Test
    public void ManagerStateBeforeAndAfterSaving() throws Exception {
        File tempFile = File.createTempFile("tasks", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task(0, "Test Task", "Test Description", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic(1, "Test Epic", "Epic Description", TaskStatus.IN_PROGRESS);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(2, "Test Subtask", "Subtask Description", epic.getId(), TaskStatus.DONE);
        manager.createSubtask(subtask);

        int originalTaskCount = manager.getTasks().size();
        int originalEpicCount = manager.getEpics().size();
        int originalSubtaskCount = manager.getSubtasks().size();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(originalTaskCount, loadedManager.getTasks().size(), "Task counts do not match.");
        assertEquals(originalEpicCount, loadedManager.getEpics().size(), "Epic counts do not match.");
        assertEquals(originalSubtaskCount, loadedManager.getSubtasks().size(), "Subtask counts do not match.");

        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Tasks do not match.");
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Epics do not match.");
        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Subtasks do not match.");
    }

    @Test
    void testManagerStateBeforeAndAfterSaving() {
        Task task = new Task(1, "Test Task", "Test Description", TaskStatus.NEW);
        manager.createTask(task);

        Epic epic = new Epic(2, "Test Epic", "Epic Description", TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask(3, "Test Subtask", "Subtask Description", epic.getId(), TaskStatus.IN_PROGRESS);
        manager.createSubtask(subtask);

        int originalTaskCount = manager.getTasks().size();
        int originalEpicCount = manager.getEpics().size();
        int originalSubtaskCount = manager.getSubtasks().size();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(originalTaskCount, loadedManager.getTasks().size(), "Количество задач должно совпадать.");
        assertEquals(originalEpicCount, loadedManager.getEpics().size(), "Количество эпиков должно совпадать.");
        assertEquals(originalSubtaskCount, loadedManager.getSubtasks().size(), "Количество подзадач должно совпадать.");

        assertEquals(manager.getTasks(), loadedManager.getTasks(), "Задачи должны совпадать.");
        assertEquals(manager.getEpics(), loadedManager.getEpics(), "Эпики должны совпадать.");
        assertEquals(manager.getSubtasks(), loadedManager.getSubtasks(), "Подзадачи должны совпадать.");
    }
}
