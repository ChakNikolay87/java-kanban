package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.Map;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

    Task addTask(Task task);

    Epic addEpic(Epic epic);

    Subtask addSubtask(Subtask subtask);


    Task updateTask(Task taskToReplace);

    Epic updateEpic(Epic epicToReplace);

    Subtask updateSubtask(Subtask subtaskToReplace);


    void clearTasks();

    void clearEpics();

    void clearSubtasks();


    void deleteTask(int id);

    void deleteEpic(int id);

    void deleteSubtask(int id);


    Optional<Task> getTask(int id);

    Optional<Epic> getEpic(int id);

    Optional<Subtask> getSubtaskById(int id);


    Map<Integer, Task> getTasks();

    Map<Integer, Epic> getEpics();

    Map<Integer, Subtask> getSubtasks();


    List<Subtask> getSubtasksOfEpic(Epic epic);

    List<Task> getHistory();
}
