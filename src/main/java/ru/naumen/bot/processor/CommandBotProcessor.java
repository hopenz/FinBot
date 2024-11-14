package ru.naumen.bot.processor;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.naumen.bot.command.Commands;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.service.BalanceService;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.IncomeService;
import ru.naumen.bot.service.UserService;

import java.util.*;

/**
 * Класс {@link  CommandBotProcessor}, в котором происходит
 * обработка команд, полученных от пользователей через бот.
 */
@Component
public class CommandBotProcessor {

    /**
     * Контроллер для взаимодействия с ботом.
     */
    private final BotController botController;

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Сервис для взаимодействия с балансом.
     */
    private final BalanceService balanceService;

    /**
     * Сервис для взаимодействия с доходами.
     */
    private final IncomeService incomeService;

    /**
     * Сервис для взаимодействия с расходами.
     */
    private final ExpenseService expenseService;

    /**
     * Map для хранения команд, где ключ это текст команды, а значение соответствующий экземпляр {@link Commands}
     */
    private final Map<String, Commands> commandsMap = new Hashtable<>();

    /**
     * Конструктор CommandBotProcessor.
     *
     * @param botController  сервис для взаимодействия с ботом.
     * @param userService    сервис для взаимодействия с данными пользователя.
     * @param balanceService сервис для взаимодействия с балансом.
     * @param incomeService  сервис для взаимодействия с доходами.
     * @param expenseService сервис для взаимодействия с расходами.
     */
    public CommandBotProcessor(@Lazy BotController botController, UserService userService, BalanceService balanceService,
                               IncomeService incomeService, ExpenseService expenseService) {
        this.botController = botController;
        this.userService = userService;
        this.balanceService = balanceService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;

        for (Commands command : Commands.values()) {
            commandsMap.put(command.getCommand(), command);
        }
    }

    /**
     * Обработка команды, полученной от пользователя
     *
     * @param message сообщение от пользователя, содержащее команду
     * @param chatId  идентификатор чата, в котором была отправлена команда
     */
    public void processCommand(String message, long chatId) {
        if(commandsMap.containsKey(message)){
            executeCommand(commandsMap.get(message), chatId);
        }else {
            handleOther(chatId);
        }
    }

    /**
     * Выполнение команды
     *
     * @param command команда, которую необходимо выполнить
     * @param chatId  идентификатор чата, в котором была отправлена команда
     */
    private void executeCommand(Commands command, long chatId) {
        switch (command) {
            case START_COMMAND -> handleStart(chatId);
            case EXPENSES_COMMAND -> handleExpenses(chatId);
            case INCOME_COMMAND -> handleIncome(chatId);
            case HELP_COMMAND -> handleHelp(chatId);
            case BALANCE_COMMAND -> handleBalance(chatId);
        }
    }

    /**
     * Отправка текущего баланса пользователя
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleBalance(long chatId) {
        botController.sendMessage("Ваш баланс: " + balanceService.getBalance(chatId), chatId);
    }

    /**
     * Отправка справочной информации по командам и шаблонов добавления доходов/расходов
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleHelp(long chatId) {
        Commands[] arrayOfCommand = Commands.values();
        StringBuilder stringHelp = new StringBuilder("Справка по всем командам:\n");
        for (Commands command : arrayOfCommand) {
            stringHelp.append(command.getCommand()).append(" - ")
                    .append(command.getDescription()).append("\n");
        }
        stringHelp.append("\nЧтобы добавить доход введите:\n+ <сумма> <описание>\n");
        stringHelp.append("\nЧтобы добавить расход введите:\n- <сумма> <описание>");
        botController.sendMessage(stringHelp.toString(), chatId);
    }

    /**
     * Отправка списка доходов пользователя
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleIncome(long chatId) {
        List<Income> incomeList = incomeService.getIncomes(chatId);
        StringBuilder sb = new StringBuilder();
        sb.append("Ваши доходы:\n");
        for (Income income : incomeList) {
            sb.append(income.amount()).append(" - ").append(income.description()).append("\n");
        }
        botController.sendMessage(sb.toString(), chatId);
    }

    /**
     * Отправка списка расходов пользователя
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleExpenses(long chatId) {
        List<Expense> expenseList = expenseService.getExpenses(chatId);
        StringBuilder sb = new StringBuilder();
        sb.append("Ваши расходы:\n");
        for (Expense expense : expenseList) {
            sb.append(expense.amount()).append(" - ").append(expense.description()).append("\n");
        }
        botController.sendMessage(sb.toString(), chatId);
    }

    /**
     * Начало взаимодействия с ботом, отправка приветственного сообщения
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleStart(long chatId) {
        userService.openChat(chatId);
        botController.sendMessage("Давайте начнём", chatId);
    }

    /**
     * Уведомление пользователя о неизвестной команде
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleOther(long chatId) {
        botController.sendMessage("Неизвестная команда", chatId);
    }

    /**
     * Проверка, открыт ли чат с пользователем или будет открыт
     * после обработки текущего сообщения(в случае, если сообщение является командой /start)
     *
     * @param message сообщение от пользователя
     * @param chatId  идентификатор чата, в котором была отправлена команда
     * @return {@code  true}, чат с пользователем открыт, либо текущее сообщение является командой /start,
     * {@code false} в противном случае
     */
    public boolean isChatActiveOrStarting(String message, long chatId) {
        if (!Commands.START_COMMAND.getCommand().equals(message) && !userService.isChatOpened(chatId)) {
            botController.sendMessage(
                    "Чтобы начать работу, нажмите " + Commands.START_COMMAND.getCommand(), chatId
            );
            return false;
        }
        return true;
    }
}
