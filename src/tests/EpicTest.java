package tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class EpicTest {
    private Epic epic;

    @BeforeEach
    public void setUp() {
        epic = new Epic(1, "Epic 1", "Description 1");
    }

    @Test
    public void testGetSubtasks() {
        List<Subtask> subtasks = epic.getSubtasks();
        assertNotNull(subtasks);
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void testAddSubtask() {
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", epic.getId());
        epic.addSubtask(subtask);
        List<Subtask> subtasks = epic.getSubtasks();
        assertEquals(1, subtasks.size());
        assertEquals(subtask, subtasks.get(0));
    }

    @Test
    public void testRemoveSubtask() {
        Subtask subtask = new Subtask(2, "Subtask 1", "Description 1", epic.getId());
        epic.addSubtask(subtask);
        epic.removeSubtask(subtask);
        List<Subtask> subtasks = epic.getSubtasks();
        assertTrue(subtasks.isEmpty());
    }
}
