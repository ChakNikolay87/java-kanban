import com.google.gson.Gson;
import http.GsonUtil;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PrioritizedTasksHandlerTest {

    private Gson gson;
    private TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        gson = GsonUtil.createGson();
        taskManager = new InMemoryTaskManager();
        ((InMemoryTaskManager) taskManager).resetIdCounter();
    }

    @Test
    public void testGetPrioritizedTasksWithTasks() {
        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(60), LocalDateTime.of(2024, 10, 11, 10, 0));
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(90), LocalDateTime.of(2024, 10, 11, 12, 0));

        taskManager.addTask(task1);
        taskManager.addTask(task2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String json = gson.toJson(prioritizedTasks);

        String expectedJson = "[{\"name\":\"Task 1\",\"description\":\"Description 1\",\"id\":1,\"status\":\"NEW\",\"duration\":\"PT1H\",\"startTime\":\"2024-10-11T10:00:00\"}," +
                "{\"name\":\"Task 2\",\"description\":\"Description 2\",\"id\":2,\"status\":\"NEW\",\"duration\":\"PT1H30M\",\"startTime\":\"2024-10-11T12:00:00\"}]";

        assertEquals(expectedJson, json);
    }
}
