package test;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

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
        assertEquals(1, task.getId());
        assertEquals("Task 1", task.getName());
        assertEquals("Description 1", task.getDescription());

        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    void createEpic() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        assertNotNull(epic);
        assertEquals(1, epic.getId());
        assertEquals("Epic 1", epic.getName());
        assertEquals("Description 1", epic.getDescription());

        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);

        assertNotNull(subtask);
        assertEquals(2, subtask.getId());
        assertEquals("Subtask 1", subtask.getName());
        assertEquals("Description 1", subtask.getDescription());
        assertEquals(epic.getId(), subtask.getEpicId());

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));

        List<Subtask> epicSubtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, epicSubtasks.size());
        assertEquals(subtask, epicSubtasks.get(0));
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
    void getTaskById() {
        Task task = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask, retrievedSubtask);
    }

    @Test
    void updateTask() {
        Task task = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task);
        task.setDescription("Updated Description");
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTasks().get(0);
        assertEquals("Updated Description", updatedTask.getDescription());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic(0, "Epic 1", "Description");
        taskManager.createEpic(epic);
        epic.setDescription("Updated Description");
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpics().get(0);
        assertEquals("Updated Description", updatedEpic.getDescription());
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
        assertEquals("Updated Description", updatedSubtask.getDescription());
    }

    @Test
    void deleteTaskById() {
        Task task = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(epic.getId());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }


    @Test
    void deleteAllTasks() {
        Task task1 = new Task(1, "Task 1", "Description 1");
        Task task2 = new Task(2, "Task 2", "Description 2");
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "All tasks should be deleted.");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1");
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", epic1.getId());
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);

        taskManager.deleteAllEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "All epics should be deleted.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "All subtasks should be deleted along with epics.");
    }

    @Test
    void deleteAllSubtasks() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1");
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", epic1.getId());
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);

        taskManager.deleteAllSubtasks();

        assertTrue(taskManager.getSubtasks().isEmpty(), "All subtasks should be deleted.");
        assertEquals(TaskStatus.NEW, epic1.getStatus(), "Epic status should be NEW after deleting all subtasks.");
    }


    @Test
    void getSubtasksByEpicId() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
    }

    @Test
    void getHistory() {
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
    void taskEqualityById() {
        Task task1 = new Task(0, "Task 1", "Description 1");
        taskManager.createTask(task1);
        Task task2 = new Task(0, "Task 2", "Description 2");
        taskManager.createTask(task2);

        assertEquals(task1, taskManager.getTaskById(task1.getId()));
        assertEquals(task2, taskManager.getTaskById(task2.getId()));
    }

    @Test
    void epicCannotAddItselfAsSubtask() {
        Epic epic = new Epic(0, "Epic 1", "Description 1");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);

        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());

        Assertions.assertNull(retrievedSubtask, "Subtask should not be added if an epic tries to add itself as a subtask.");
    }



}