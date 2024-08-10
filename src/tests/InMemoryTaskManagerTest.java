package tests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void createTask() {
        Task task = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task);
        assertNotNull(task);
        Assertions.assertEquals(1, task.getId());
        Assertions.assertEquals("Task 1", task.getName());
        Assertions.assertEquals("Description 1", task.getDescription());

        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        Assertions.assertEquals(task, tasks.get(0));
    }

    @Test
    void createEpic() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        assertNotNull(epic);
        Assertions.assertEquals(1, epic.getId());
        Assertions.assertEquals("Epic 1", epic.getName());
        Assertions.assertEquals("Description 1", epic.getDescription());

        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        Assertions.assertEquals(epic, epics.get(0));
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);

        assertNotNull(subtask);
        Assertions.assertEquals(2, subtask.getId());
        Assertions.assertEquals("Subtask 1", subtask.getName());
        Assertions.assertEquals("Description 1", subtask.getDescription());
        Assertions.assertEquals(epic.getId(), subtask.getEpicId());

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
        Assertions.assertEquals(subtask, subtasks.get(0));

        List<Subtask> epicSubtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, epicSubtasks.size());
        Assertions.assertEquals(subtask, epicSubtasks.get(0));
    }

    @Test
    void getTasks() {
        Task task = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task);
        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
    }

    @Test
    void getEpics() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
    }

    @Test
    void getSubtasks() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
    }

    @Test
    void testGetTaskById() {
        Task task = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        Assertions.assertEquals(task, retrievedTask);
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        Assertions.assertEquals(epic, retrievedEpic);
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        Assertions.assertEquals(subtask, retrievedSubtask);
    }

    @Test
    void updateTask() {
        Task task = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task);
        task.setDescription("Updated Description");
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTasks().get(0);
        Assertions.assertEquals("Updated Description", updatedTask.getDescription());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic(0, "Epic 1", "Description");
        taskManager.createEpic(epic);
        epic.setDescription("Updated Description");
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpics().get(0);
        Assertions.assertEquals("Updated Description", updatedEpic.getDescription());
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic(0, "Epic 1", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description", epic.getId());
        taskManager.createSubtask(subtask);
        subtask.setDescription("Updated Description");
        taskManager.updateSubtask(subtask);
        Subtask updatedSubtask = taskManager.getSubtasks().get(0);
        Assertions.assertEquals("Updated Description", updatedSubtask.getDescription());
    }

    @Test
    void deleteTaskById() {
        Task task = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());
        Assertions.assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(epic.getId());
        Assertions.assertTrue(taskManager.getEpics().isEmpty());
        Assertions.assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void testDeleteSubtaskById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());
        Assertions.assertTrue(taskManager.getSubtasks().isEmpty());
    }


    @Test
    void testGetSubtasksByEpicId() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
    }

    @Test
    void testGetHistory() {
        Task task1 = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task1);
        Task task2 = new Task(0, "Task 2", "Description 2");
        taskManager.createTask(task2);
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
    }

    @Test
    void testTaskEqualityById() {
        Task task1 = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task1);
        Task task2 = new Task(0, "Task 2", "Description 2");
        taskManager.createTask(task2);

        Assertions.assertEquals(task1, taskManager.getTaskById(task1.getId()));
        Assertions.assertEquals(task2, taskManager.getTaskById(task2.getId()));
    }

    @Test
    void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertNull(retrievedSubtask, "Subtask should not be added if an epic tries to add itself as a subtask.");
    }



}