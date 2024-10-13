package managers;

import history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private static int nextId = 1;
    protected final Map<Integer, Task> tasksMap = new HashMap<>();
    protected final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private final TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));

    @Override
    public Task addTask(Task task) {
        if (isOverlapping(task)) {
            throw new IllegalArgumentException("Задачи пересекаются по времени выполнения.");
        }
        task.setId(nextId);
        nextId++;
        tasksMap.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(nextId);
        nextId++;
        epicsMap.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Epic epic = epicsMap.get(subtask.getSubtasksEpicId());
        if (epic == null) {
            throw new IllegalArgumentException("Эпик с ID " + subtask.getSubtasksEpicId() + " не найден.");
        }
        if (isOverlapping(subtask)) {
            throw new IllegalArgumentException("Задачи пересекаются по времени выполнения.");
        }
        subtask.setId(nextId);
        nextId++;
        subtasksMap.put(subtask.getId(), subtask);
        epic.addSubtask(subtask);
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Task updateTask(Task taskToReplace) {
        if (tasksMap.containsKey(taskToReplace.getId())) {
            Task existingTask = tasksMap.get(taskToReplace.getId());

            prioritizedTasks.remove(existingTask);

            if (isOverlapping(taskToReplace)) {
                prioritizedTasks.add(existingTask);
                throw new IllegalArgumentException("Task time conflicts with existing tasks.");
            }

            tasksMap.replace(taskToReplace.getId(), taskToReplace);
            prioritizedTasks.add(taskToReplace);
        }
        return taskToReplace;
    }




    @Override
    public Subtask updateSubtask(Subtask subtaskToReplace) {
        var updatingSubtaskId = subtaskToReplace.getId();

        if (!subtasksMap.containsKey(updatingSubtaskId)) {
            throw new IllegalArgumentException("Subtask by id=%s not found".formatted(updatingSubtaskId));
        }

        Subtask existingSubtask = subtasksMap.get(updatingSubtaskId);

        prioritizedTasks.remove(existingSubtask);

        if (isOverlapping(subtaskToReplace)) {
            prioritizedTasks.add(existingSubtask);
            throw new IllegalArgumentException("Subtask time conflicts with existing tasks.");
        }

        subtasksMap.replace(updatingSubtaskId, subtaskToReplace);

        var epic = epicsMap.get(subtaskToReplace.getSubtasksEpicId());

        if (epic != null) {
            epic.updateSubtask(subtaskToReplace);
        }

        prioritizedTasks.add(subtaskToReplace);

        return subtaskToReplace;
    }



    @Override
    public Epic updateEpic(Epic epicToReplace) {
        Epic existingEpic = epicsMap.get(epicToReplace.getId());
        if (existingEpic != null) {
            for (Subtask subtask : existingEpic.getSubtasks()) {
                epicToReplace.addSubtask(subtask);
            }
            epicsMap.replace(epicToReplace.getId(), epicToReplace);
            epicToReplace.updateEpicTime();
        }
        return epicToReplace;
    }

    @Override
    public void clearTasks() {
        for (Integer taskKey : tasksMap.keySet()) {
            inMemoryHistoryManager.remove(taskKey);
        }
        tasksMap.clear();
    }

    @Override
    public void clearEpics() {
        for (Integer epicKey : epicsMap.keySet()) {
            inMemoryHistoryManager.remove(epicKey);
        }
        for (Integer subtaskKey : subtasksMap.keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        for (Epic epic : epicsMap.values()) {
            epic.clear();
        }
        epicsMap.clear();
        subtasksMap.clear();
    }

    @Override
    public void clearSubtasks() {
        for (Integer subtaskKey : subtasksMap.keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.clear();
        }
    }

    @Override
    public void deleteTask(int id) {
        inMemoryHistoryManager.remove(id);
        tasksMap.remove(id);
    }

    @Override
    public void deleteEpic(int id) {
        Epic epic = epicsMap.get(id);
        if (epic != null) {
            for (Subtask subtask : epic.getSubtasks()) {
                inMemoryHistoryManager.remove(subtask.getId());
                subtasksMap.remove(subtask.getId());
            }
            inMemoryHistoryManager.remove(id);
            epicsMap.remove(id);
        }
    }

    @Override
    public void deleteSubtask(int id) {
        Subtask subtask = subtasksMap.remove(id);
        if (subtask != null) {
            inMemoryHistoryManager.remove(id);
            Epic epic = epicsMap.get(subtask.getSubtasksEpicId());
            if (epic != null) {
                epic.removeSubtask(id);
            }
        }
    }

    @Override
    public Optional<Task> getTask(int id) {
        Optional<Task> findTask = tasksMap.values().stream()
                .filter(task -> id == task.getId())
                .findFirst();
        findTask.ifPresent(task -> inMemoryHistoryManager.add(task));
        return findTask;
    }

    @Override
    public Optional<Epic> getEpic(int id) {
        Optional<Epic> findEpic = epicsMap.values().stream()
                .filter(epic -> id == epic.getId())
                .findFirst();
        findEpic.ifPresent(epic -> inMemoryHistoryManager.add(epic));
        return findEpic;
    }

    @Override
    public Optional<Subtask> getSubtaskById(int id) {
        Optional<Subtask> findSubtask = subtasksMap.values().stream()
                .filter(subtask -> subtask.getId() == id)
                .findFirst();
        findSubtask.ifPresent(subtask -> inMemoryHistoryManager.add(subtask));
        return findSubtask;
    }

    @Override
    public List<Task> getTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    @Override
    public List<Epic> getEpics() {
        return new ArrayList<>(epicsMap.values());
    }

    @Override
    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasksMap.values());
    }

    @Override
    public List<Subtask> getSubtasksOfEpic(Epic epic) {
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public void resetIdCounter() {
        InMemoryTaskManager.nextId = 1;
    }


    public void setNextId(int nextId) {
        InMemoryTaskManager.nextId = nextId;
    }

    @Override
    public boolean isOverlapping(Task newTask) {
        return getPrioritizedTasks().stream()
                .anyMatch(existingTask ->
                        existingTask.getStartTime() != null && existingTask.getEndTime() != null &&
                                newTask.getStartTime() != null && newTask.getEndTime() != null &&
                                !(existingTask.getEndTime().isBefore(newTask.getStartTime()) ||
                                        existingTask.getStartTime().isAfter(newTask.getEndTime()))
                );
    }

}
