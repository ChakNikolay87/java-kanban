import com.google.gson.Gson;
import http.GsonUtil;
import http.HttpTaskServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpSubTaskManagerTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = GsonUtil.createGson();

        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void addSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic for subtask testing");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Testing subtask creation", epic.getId(), Duration.ofMinutes(15), LocalDateTime.now());
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Некорректный статус ответа при создании подзадачи");

        List<Subtask> subtasksFromManager = manager.getSubtasks();
        assertNotNull(subtasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, subtasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("Test Subtask", subtasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }


    @Test
    public void getSubtasks() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic for subtask testing");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Existing subtask description", epic.getId(), Duration.ofMinutes(10), LocalDateTime.now());
        manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный статус ответа при запросе подзадач");

        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), List.class);
        assertEquals(1, subtasksFromResponse.size(), "Некорректное количество подзадач в ответе");
    }

    @Test
    public void deleteSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Epic for subtask testing");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Subtask to Delete", "Subtask description", epic.getId(), Duration.ofMinutes(5), LocalDateTime.now());
        Subtask createdSubtask = manager.addSubtask(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + createdSubtask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Некорректный статус ответа при удалении подзадачи");

        assertEquals(0, manager.getSubtasks().size(), "Подзадача не была удалена");
    }

    @Test
    public void deleteNonExistingSubtask() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/999");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный статус ответа при удалении несуществующей подзадачи");
    }
}
