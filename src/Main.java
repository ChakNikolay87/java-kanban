import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import status.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Scanner;

public class Main {
    static Scanner scanner;

    public static void main(String[] args) {
        TaskManager inMemoryTaskManager = Managers.getDefault();

        Task task1 = new Task("Переезд",
                "Собрать вещи",
                Duration.ofMinutes(60),
                LocalDateTime.of(2024, 9, 23, 10, 20));
        Task task2 = new Task("Стрижка",
                "Сходить в барбершоп",
                Duration.ofHours(3),
                LocalDateTime.of(2024, 9, 24, 17, 0));

        Epic epic1 = new Epic("Чертежи моста", "Сделать проект моста через реку Волга");
        Epic epic2 = new Epic("Командировка", "Подготовиться к командировке");

        Subtask subtask11 = new Subtask("Пролетное строение",
                "Начертить пролетное строение",
                3,
                Duration.ofDays(14),
                LocalDateTime.of(2024, 10, 13, 8, 0));
        Subtask subtask12 = new Subtask("Опоры",
                "Начертить опоры",
                3,
                Duration.ofDays(8),
                LocalDateTime.of(2024, 10, 28, 8, 0));
        Subtask subtask21 = new Subtask("Билеты на самолет",
                "Купить билеты на самолет",
                4,
                Duration.ofMinutes(10),
                LocalDateTime.of(2024, 11, 25, 14, 5));

        scanner = new Scanner(System.in);

        while (true) {
            printMenu();
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    inMemoryTaskManager.addTask(task1);
                    inMemoryTaskManager.addTask(task2);
                    break;
                case "2":
                    System.out.println(inMemoryTaskManager.getTasks().values());
                    break;
                case "3":
                    inMemoryTaskManager.clearTasks();
                    System.out.println("Все задачи удалены.");
                    System.out.println(inMemoryTaskManager.getTasks().values());
                    break;
                case "4":
                    System.out.println(inMemoryTaskManager.getTask(1));
                    break;
                case "5":
                    Task task3 = new Task("Переезд продолжение",
                            "Собрать оставшиеся вещи",
                            task1.getId(),
                            Status.DONE,
                            Duration.ofMinutes(60),
                            LocalDateTime.of(2024, 9, 23, 10, 20));
                    inMemoryTaskManager.updateTask(task3);
                    System.out.println("Задача обновлена.");
                    break;
                case "6":
                    inMemoryTaskManager.deleteTask(1);
                    System.out.println("Задача удалена.");
                    break;
                case "7":
                    inMemoryTaskManager.addEpic(epic1);
                    inMemoryTaskManager.addEpic(epic2);
                    break;
                case "8":
                    System.out.println(inMemoryTaskManager.getEpics().values());
                    break;
                case "9":
                    inMemoryTaskManager.clearEpics();
                    System.out.println("Все эпики удалены.");
                    break;
                case "10":
                    System.out.println(inMemoryTaskManager.getEpic(3));
                    break;
                case "11":
                    Epic epic3 = new Epic("Чертежи нового арочного моста",
                            "Сделать часть нового проекта Волга",
                            epic1.getId());
                    inMemoryTaskManager.updateEpic(epic3);
                    System.out.println("Эпик обновлен.");
                    break;
                case "12":
                    inMemoryTaskManager.deleteEpic(3);
                    System.out.println("Эпик удален.");
                    break;
                case "13":
                    inMemoryTaskManager.addSubtask(subtask11);
                    inMemoryTaskManager.addSubtask(subtask12);
                    inMemoryTaskManager.addSubtask(subtask21);
                    break;
                case "14":
                    System.out.println(inMemoryTaskManager.getSubtasks().values());
                    break;
                case "15":
                    inMemoryTaskManager.clearSubtasks();
                    System.out.println("Все подзадачи удалены.");
                    break;
                case "16":
                    System.out.println(inMemoryTaskManager.getSubtaskById(5));
                    break;
                case "17":
                    Subtask subtask13 = new Subtask(subtask11.getId(),
                            "Пролетное строение",
                            "Начертить пролетное строение",
                            Status.INPROGRESS,
                            subtask11.getSubtasksEpicId(),
                            subtask11.getDuration(),
                            subtask11.getStartTime());
                    inMemoryTaskManager.updateSubtask(subtask13);
                    System.out.println("Подзадача обновлена.");
                    break;
                case "18":
                    inMemoryTaskManager.deleteSubtask(5);
                    System.out.println("Подзадача удалена.");
                    break;
                case "19":
                    System.out.println(inMemoryTaskManager.getSubtasksOfEpic(epic1));
                    System.out.println(inMemoryTaskManager.getSubtasksOfEpic(epic2));
                    break;
                case "20":
                    System.out.println(inMemoryTaskManager.getHistory());
                    break;
                case "21":
                    System.out.println("Выход");
                    return;
                default:
                    System.out.println("Неверная команда. Попробуйте еще раз.");
                    break;
            }
        }
    }

    private static void printMenu() {
        System.out.println("Выберите команду:");
        System.out.println("1 - Добавить задачу в список");
        System.out.println("2 - Посмотреть список задач");
        System.out.println("3 - Очистить список задач");
        System.out.println("4 - Получить задачу по id");
        System.out.println("5 - Обновить статус задачи");
        System.out.println("6 - Удалить задачу");
        System.out.println("7 - Добавить эпик в список");
        System.out.println("8 - Посмотреть список эпиков");
        System.out.println("9 - Очистить список эпиков");
        System.out.println("10 - Получить эпик по id");
        System.out.println("11 - Обновить эпик");
        System.out.println("12 - Удалить эпик");
        System.out.println("13 - Добавить подзадачу в эпик");
        System.out.println("14 - Посмотреть список всех подзадач всех эпиков");
        System.out.println("15 - Очистить список всех подзадач всех эпиков");
        System.out.println("16 - Получить подзадачу по id");
        System.out.println("17 - Обновить статус подзадачи");
        System.out.println("18 - Удалить подзадачу");
        System.out.println("19 - Посмотреть список подзадач эпика");
        System.out.println("20 - Запрос истории просмотров");
        System.out.println("21 - Выход");
    }
}
