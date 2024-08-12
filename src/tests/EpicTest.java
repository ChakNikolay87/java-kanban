package tests;

import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EpicTest {
    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    public void setUp() {
        taskManager = Managers.getDefault();
        epic = new Epic(1, "Epic 1", "Description 1");
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
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    void testRemoveSubtask() {
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", epic.getId());
        taskManager.createSubtask(subtask);

        taskManager.deleteSubtaskById(subtask.getId());

        List<Subtask> subtasks = taskManager.getSubtasksByEpicId(epic.getId());
        assertTrue(subtasks.isEmpty());
    }
}
