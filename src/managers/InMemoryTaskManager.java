package managers;

import history.HistoryManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import status.Status;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    private static int nextId = 1;
    protected final Map<Integer, Task> tasksMap = new HashMap<>();
    protected final Map<Integer, Epic> epicsMap = new HashMap<>();
    protected final Map<Integer, Subtask> subtasksMap = new HashMap<>();
    private TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
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
        if (isOverlapping(subtask)) {
            throw new IllegalArgumentException("Задачи пересекаются по времени выполнения.");
        }
        subtask.setId(nextId);
        nextId++;
        subtasksMap.put(subtask.getId(), subtask);
        if (epicsMap.get(subtask.getSubtasksEpicId()) == null) {
            throw new IllegalArgumentException("Эпик с ID " + subtask.getSubtasksEpicId() + " не найден.");
        }
        epicsMap.get(subtask.getSubtasksEpicId()).put(subtask);
        prioritizedTasks.add(subtask);
        return subtask;
    }

    @Override
    public Task updateTask(Task taskToReplace) {
        if (tasksMap.containsKey(taskToReplace.getId())) {
            tasksMap.replace(taskToReplace.getId(), taskToReplace);
        }
        return taskToReplace;
    }

    @Override
    public Epic updateEpic(Epic epicToReplace) {
        epicToReplace.setEpicSubtusks(epicsMap.get(epicToReplace.getId()).getEpicSubtusks());
        if (epicsMap.containsKey(epicToReplace.getId())) {
            Status statusEpicToReplace = epicsMap.get(epicToReplace.getId()).getStatus();
            epicsMap.replace(epicToReplace.getId(), epicToReplace);
            epicsMap.get(epicToReplace.getId()).setStatus(statusEpicToReplace);
            epicsMap.get(epicToReplace.getId()).updateEpicTime();
        }
        return epicToReplace;
    }

    @Override
    public Subtask updateSubtask(Subtask subtaskToReplace) {
        if (subtasksMap.containsKey(subtaskToReplace.getId())) {
            subtasksMap.replace(subtaskToReplace.getId(), subtaskToReplace);
        }
        if (epicsMap.get(subtaskToReplace.getSubtasksEpicId()).getEpicSubtusks()
                .containsKey(subtaskToReplace.getId())) {
            epicsMap.get(subtaskToReplace.getSubtasksEpicId()).getEpicSubtusks()
                    .replace(subtaskToReplace.getId(), subtaskToReplace);
        }
        epicsMap.get(subtaskToReplace.getSubtasksEpicId()).setStatus(epicsMap.get(subtaskToReplace.getSubtasksEpicId())
                .isSubtasksDone());
        return subtaskToReplace;
    }


    @Override
    public Map<Integer, Task> deleteTask(int id) {
        inMemoryHistoryManager.remove(id);
        tasksMap.remove(id);
        return tasksMap;
    }


    @Override
    public Map<Integer, Epic> deleteEpic(int id) {
        inMemoryHistoryManager.remove(id);
        epicsMap.get(id)
                .getEpicSubtusks()
                .keySet()
                .forEach(inMemoryHistoryManager::remove);
        epicsMap.get(id).getEpicSubtusks().clear();
        subtasksMap.entrySet().removeIf(entry -> entry.getValue().getSubtasksEpicId() == id);
        epicsMap.remove(id);
        return epicsMap;
    }


    @Override
    public Map<Integer, Subtask> deleteSubtask(int id) {
        inMemoryHistoryManager.remove(id);
        subtasksMap.remove(id);
        epicsMap.values().forEach(epic ->
                epic.getEpicSubtusks()
                        .values()
                        .removeIf(subtask -> subtask.getId() == id));
        return subtasksMap;
    }


    @Override
    public Map<Integer, Task> clearTasks() {
        for (Integer taskKey : tasksMap.keySet()) {
            inMemoryHistoryManager.remove(taskKey);
        }
        tasksMap.clear();
        return tasksMap;
    }


    @Override
    public Map<Integer, Epic> clearEpics() {
        for (Integer epicKey : epicsMap.keySet()) {
            inMemoryHistoryManager.remove(epicKey);
        }
        for (Integer subtaskKey : subtasksMap.keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        for (Integer i : epicsMap.keySet()) {
            epicsMap.get(i).getEpicSubtusks().clear();
        }
        epicsMap.clear();
        subtasksMap.clear();
        return epicsMap;
    }


    @Override
    public Map<Integer, Subtask> clearSubtasks() {
        for (Integer subtaskKey : subtasksMap.keySet()) {
            inMemoryHistoryManager.remove(subtaskKey);
        }
        subtasksMap.clear();
        for (Epic epic : epicsMap.values()) {
            epic.clear();
        }
        return subtasksMap;
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
    public Optional<Subtask> getSubtask(int id) {
        Optional<Subtask> findSubtask = subtasksMap.values().stream()
                .filter(subtask -> id == subtask.getId())
                .findFirst();
        findSubtask.ifPresent(subtask -> {
            if (subtask != null) {
                inMemoryHistoryManager.add(subtask);
            }
        });
        return findSubtask;
    }

    @Override
    public Map<Integer, Task> printTasks() {
        return tasksMap;
    }


    @Override
    public Map<Integer, Epic> printEpics() {
        return epicsMap;
    }


    @Override
    public Map<Integer, Subtask> printSubtasks() {
        return subtasksMap;
    }


    @Override
    public List<Subtask> printSubtusksOfEpic(Epic epic) {
        return new ArrayList<>(epic.getEpicSubtusks().values());
    }


    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }


    public void setNextId(int nextId) {
        InMemoryTaskManager.nextId = nextId;
    }


    private boolean isOverlapping(Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(existingTask ->
                        existingTask.getStartTime() != null &&
                                existingTask.getEndTime() != null &&
                                !(existingTask.getEndTime().isBefore(newTask.getStartTime()) ||
                                        existingTask.getStartTime().isAfter(newTask.getEndTime()))
                );
    }

}


