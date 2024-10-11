import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpServer;
import http.GsonUtil;
import http.HistoryHandler;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import status.Status;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryHandlerTest {

    private HttpServer server;
    private TaskManager taskManager;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        gson = GsonUtil.createGson();
        server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.start();
    }



    @AfterEach
    public void tearDown() {
        server.stop(0);
    }

    private HttpURLConnection sendGetRequest(String endpoint) throws IOException {
        URL url = new URL("http://localhost:8080" + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        return connection;
    }

    @Test
    public void getHistoryWhenHistoryIsEmpty() throws IOException {
        HttpURLConnection connection = sendGetRequest("/history");

        int responseCode = connection.getResponseCode();
        String responseBody = new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

        assertEquals(204, responseCode);
        assertTrue(responseBody.isEmpty());
    }

    @Test
    public void getHistoryWithMultipleTasks() throws IOException {
        Task task1 = new Task("Task 1", "Description 1", Status.NEW, 10);
        Task task2 = new Task("Task 2", "Description 2", Status.INPROGRESS, 15);
        Task task3 = new Task("Task 3", "Description 3", Status.DONE, 20);

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        taskManager.getTask(task1.getId());
        taskManager.getTask(task2.getId());
        taskManager.getTask(task3.getId());

        HttpURLConnection connection = sendGetRequest("/history");

        assertEquals(200, connection.getResponseCode());

        String response = readResponse(connection);

        Type taskListType = new TypeToken<List<Task>>() {}.getType();
        List<Task> history = gson.fromJson(response, taskListType);

        assertEquals(3, history.size());
        assertTrue(history.contains(task1));
        assertTrue(history.contains(task2));
        assertTrue(history.contains(task3));
    }


    private String readResponse(HttpURLConnection connection) throws IOException {
        return new String(connection.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
    }
}
