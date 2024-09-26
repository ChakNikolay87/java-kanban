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


    Map<Integer, Task> clearTasks();

    Map<Integer, Epic> clearEpics();

    Map<Integer, Subtask> clearSubtasks();


    Map<Integer, Task> deleteTask(int id);

    Map<Integer, Epic> deleteEpic(int id);

    Map<Integer, Subtask> deleteSubtask(int id);


    Optional<Task> getTask(int id);

    Optional<Epic> getEpic(int id);

    Optional<Subtask> getSubtask(int id);


    Map<Integer, Task> printTasks();

    Map<Integer, Epic> printEpics();

    Map<Integer, Subtask> printSubtasks();

;
    List<Subtask> printSubtusksOfEpic(Epic epic);

    List<Task> getHistory();

    void setNextId(int id);
}
