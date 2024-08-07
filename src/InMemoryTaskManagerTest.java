import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    private TaskManager taskManager;



    @BeforeEach
    public void setUp(){
        taskManager = Managers.getDefault();

    }

    @Test
    void createTask() {
        Task task = taskManager.createTask("Task 1", "Description 1");
        assertNotNull(task);
        assertEquals("Task 1", task.getName());
        assertEquals("Description 1", task.getDescription());
    }

    @Test
    void createEpic() {
        Epic epic = taskManager.createEpic("Epic 1", "Description 1");
        assertEquals("Epic 1", epic.getName());
        assertEquals("Description 1", epic.getDescription());
    }

    @Test
    void createSubtask() {
        Epic epic = taskManager.createEpic("Epic 1", "Description 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description 1", epic.getId());
        assertNotNull(subtask);
        assertEquals("Subtask 1", subtask.getName());
        assertEquals("Description 1", subtask.getDescription());
        assertEquals(epic.getId(), subtask.getEpicId());
    }

    @Test
    void getTasks() {
        taskManager.createTask("Task 1", "Description 1");
        List<Task> tasks = taskManager.getTasks();
        assertEquals(1, tasks.size());
    }

    @Test
    void getEpics() {
        taskManager.createEpic("Epic 1", "Description 1");
        List<Epic> epics = taskManager.getEpics();
        assertEquals(1, epics.size());
    }

    @Test
    void getSubtasks() {
        Epic epic = taskManager.createEpic("Epic 1", "Description 1");
        taskManager.createSubtask("Subtask 1", "Description 1", epic.getId());
        List<Subtask> subtasks = taskManager.getSubtasks();
        assertEquals(1, subtasks.size());
    }

    @Test
    public void testGetTaskById() {
        Task task = taskManager.createTask("Task 1", "Description 1");
        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
    }


    @Test
    void getEpicById() {
        Epic epic = taskManager.createEpic("Epic 1", "Description 1");
        Epic retrievedEpic = taskManager.getEpicById(epic.getId());
        assertNotNull(retrievedEpic);
        assertEquals(epic, retrievedEpic);
    }

    @Test
    void getSubtaskById() {
        Epic epic = taskManager.createEpic("Epic 1", "Description 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description 1", epic.getId());
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertNotNull(retrievedSubtask);
        assertEquals(subtask,retrievedSubtask);
    }

    @Test
    void updateTask() {
        Task task = taskManager.createTask("Task 1", "Description 1");
        task.setDescription("Updated Description");
        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTasks().get(0);
        assertEquals("Updated Description", updatedTask.getDescription());

    }

    @Test
    void updateEpic() {
        Epic epic = taskManager.createEpic("Epic 1", "Description" );
        epic.setDescription("Updated Description");
        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpics().get(0);
        assertEquals("Updated Description", updatedEpic.getDescription());

    }

    @Test
    void updateSubtask() {
        Epic epic = taskManager.createEpic("Epic 1", "Description ");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description", epic.getId());
        subtask.setDescription("Updated Description");
        taskManager.updateSubtask(subtask);
        Subtask updatedSubtask = taskManager.getSubtasks().get(0);
        assertEquals("Updated Description", updatedSubtask.getDescription());

    }

    @Test
    void deleteTaskById() {
        Task task = taskManager.createTask("Task 1", "Description 1");
        taskManager.deleteTaskById(task.getId());
        assertTrue(taskManager.getTasks().isEmpty());
    }

    @Test
    void deleteEpicById() {
        Epic epic = taskManager.createEpic("Epic 1", "Description 1");
        taskManager.deleteEpicById(epic.getId());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }

    @Test
    public void testDeleteSubtaskById() {
        Epic epic = taskManager.createEpic("Epic 1", "Description 1");
        Subtask subtask = taskManager.createSubtask("Subtask 1", "Description 1", epic.getId());
        taskManager.deleteSubtaskById(subtask.getId());
        assertTrue(taskManager.getSubtasks().isEmpty());

    }


    @Test
    public void testDeleteAllTasks() {
        taskManager.createTask("Task 1", "Description 1");
        taskManager.deleteAllTasks();
        assertTrue(taskManager.getTasks().isEmpty());
        assertTrue(taskManager.getEpics().isEmpty());
        assertTrue(taskManager.getSubtasks().isEmpty());
    }


    @Test
    void testGetSubtasksByEpicId() {
        Epic epic = taskManager.createEpic("Epic 1", "Description 1");
        taskManager.createSubtask("Subtask 1", "Description 1", epic.getId());
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1,subtasks.size());
    }

    @Test
    void testGetHistory() {
        Task task1 = taskManager.createTask("Task 1", "Description 1");
        Task task2 = taskManager.createTask("Task 2", "Description 2");
        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
    }
}