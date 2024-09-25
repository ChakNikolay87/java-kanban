import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        epic = new Epic(1, "Epic 1", "Description 1", TaskStatus.NEW);
        taskManager.createEpic(epic);
    }

    @Test
    void testGetSubtasks() {
        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertNotNull(subtasks);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void testAddSubtask() {
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
        Subtask addedSubtask = subtasks.get(0);
        assertEquals(subtask, addedSubtask);
        assertEquals(TaskStatus.NEW, addedSubtask.getStatus());
    }

    @Test
    void testRemoveSubtask() {
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);

        taskManager.deleteSubtaskById(subtask.getId());

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    void testSubtaskStatus() {
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", epic.getId(), TaskStatus.NEW);
        taskManager.createSubtask(subtask);

        // Check the status of the subtask
        Subtask retrievedSubtask = taskManager.getSubtasksByEpicId(epic.getId()).get(0);
        assertEquals(TaskStatus.NEW, retrievedSubtask.getStatus());

        // Update the status
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        // Verify the status is updated
        Subtask updatedSubtask = taskManager.getSubtasksByEpicId(epic.getId()).get(0);
        assertEquals(TaskStatus.DONE, updatedSubtask.getStatus());
    }
}
