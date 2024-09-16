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
        Task task = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task);
        assertNotNull(task);
        assertEquals(1, task.getId());
        assertEquals("Task 1", task.getName());
        assertEquals("Description 1", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());

        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
        assertEquals(task, tasks.get(0));
    }

    @Test
    void createEpic() {
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        assertNotNull(epic);
        assertEquals(1, epic.getId());
        assertEquals("Epic 1", epic.getName());
        assertEquals("Description 1", epic.getDescription());
        assertEquals(TaskStatus.NEW, epic.getStatus());

        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
        assertEquals(epic, epics.get(0));
    }

    @Test
    void createSubtask() {
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);

        assertNotNull(subtask);
        assertEquals(2, subtask.getId());
        assertEquals("Subtask 1", subtask.getName());
        assertEquals("Description 1", subtask.getDescription());
        assertEquals(epic.getId(), subtask.getEpicId());
        assertEquals(TaskStatus.NEW, subtask.getStatus());

        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));

        List<Subtask> epicSubtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, epicSubtasks.size());
        assertEquals(subtask, epicSubtasks.get(0));
    }

    @Test
    void getTasks() {
        Task task = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task);
        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
    }

    @Test
    void getEpics() {
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
    }

    @Test
    void getSubtasks() {
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
    }

    @Test
    void getTaskById() {
        Task task = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
    }

    @Test
    void getEpicById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void getSubtaskById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask, retrievedSubtask);
    }

    @Test
    void updateTask() {
        Task task = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task);
        task.setDescription("Updated Description");
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTasks().get(0);
        assertEquals("Updated Description", updatedTask.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedTask.getStatus());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic(0, "Epic 1", "Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        epic.setDescription("Updated Description");
        epic.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpics().get(0);
        assertEquals("Updated Description", updatedEpic.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic(0, "Epic 1", "Description", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);
        subtask.setDescription("Updated Description");
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        Subtask updatedSubtask = taskManager.getSubtasks().get(0);
        assertEquals("Updated Description", updatedSubtask.getDescription());
        assertEquals(TaskStatus.DONE, updatedSubtask.getStatus());
    }

    @Test
    void deleteTaskById() {
        Task task = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(epic.getId());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtask.getId());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    void deleteAllTasks() {
        Task task1 = new Task(1, "Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task(2, "Task 2", "Description 2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.deleteAllTasks();

        assertTrue(taskManager.getTasks().isEmpty(), "All tasks should be deleted.");
    }

    @Test
    void deleteAllEpics() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1", TaskStatus.NEW);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", epic1.getId(), TaskStatus.NEW);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);

        taskManager.deleteAllEpics();

        assertTrue(taskManager.getEpics().isEmpty(), "All epics should be deleted.");
        assertTrue(taskManager.getSubtasks().isEmpty(), "All subtasks should be deleted along with epics.");
    }

    @Test
    void deleteAllSubtasks() {
        // Create an Epic and a Subtask
        Epic epic1 = new Epic(1, "Epic 1", "Description 1", TaskStatus.NEW);
        Subtask subtask1 = new Subtask(2, "Subtask 1", "Description 1", epic1.getId(), TaskStatus.NEW);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);

        // Delete all subtasks
        taskManager.deleteAllSubtasks();

        // Assertions
        assertTrue(taskManager.getSubtasks().isEmpty(), "All subtasks should be deleted.");
        // We need to re-fetch the epic to check its status since we can't directly check the status of the original epic object
        Epic updatedEpic = taskManager.getEpicById(epic1.getId());
        assertNotNull(updatedEpic, "Epic should still exist.");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(), "Epic status should be NEW after deleting all subtasks.");
    }

    @Test
    void getSubtasksByEpicId() {
        // Create an Epic and a Subtask
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(0, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);

        // Fetch subtasks by epic ID
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());

        // Assertions
        assertEquals(1, subtasks.size(), "The number of subtasks should be 1.");
        assertEquals(subtask, subtasks.get(0), "The fetched subtask should match the created subtask.");
    }

    @Test
    void getHistory() {
        // Create and store tasks
        Task task1 = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task(0, "Task 2", "Description 2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Access tasks to add them to history
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());

        // Fetch history
        List<Task> history = taskManager.getHistory();

        // Assertions
        assertEquals(2, history.size(), "History should contain 2 tasks.");
        assertTrue(history.contains(task1), "History should contain Task 1.");
        assertTrue(history.contains(task2), "History should contain Task 2.");
    }

    @Test
    void taskEqualityById() {
        // Create and store tasks
        Task task1 = new Task(0, "Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task(0, "Task 2", "Description 2", TaskStatus.NEW);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        // Fetch tasks by ID
        Task fetchedTask1 = taskManager.getTaskById(task1.getId());
        Task fetchedTask2 = taskManager.getTaskById(task2.getId());

        // Assertions
        assertEquals(task1, fetchedTask1, "Task fetched by ID should be equal to the original Task 1.");
        assertEquals(task2, fetchedTask2, "Task fetched by ID should be equal to the original Task 2.");
    }

    @Test
    void epicCannotAddItselfAsSubtask() {
        // Create an Epic
        Epic epic = new Epic(0, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);

        // Attempt to create a Subtask with the same ID as the Epic (should not be allowed)
        Subtask subtask = new Subtask(1, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);

        // Attempt to fetch the Subtask
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());

        // Assertions
        Assertions.assertNull(retrievedSubtask, "Subtask should not be created if it has the same ID as an Epic.");
    }
}
