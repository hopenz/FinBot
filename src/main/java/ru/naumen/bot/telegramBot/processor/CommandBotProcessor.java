package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.BotCommand;
import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.telegramBot.command.Commands;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.BalanceService;
import ru.naumen.bot.telegramBot.service.ExpenseService;
import ru.naumen.bot.telegramBot.service.IncomeService;
import ru.naumen.bot.telegramBot.service.UserService;

import java.util.List;

import static ru.naumen.bot.telegramBot.command.Commands.*;

/**
 * Класс {@link  CommandBotProcessor}, в котором происходит
 * обработка команд, полученных от пользователей
 * через бота Telegram.
 */
@Component
public class CommandBotProcessor {

    /**
     * Контроллер для взаимодействия с ботом Телеграмм.
     */
    private final TelegramBotController telegramBotController;

    /**
     * Сервис для взаимодействия с данными пользователя.
     */
    private final UserService userService;

    /**
     * Сервис для взаимодействия с балансом.
     */
    public final BalanceService balanceService;

    /**
     * Сервис для взаимодействия с доходами.
     */
    public final IncomeService incomeService;

    /**
     * Сервис для взаимодействия с расходами.
     */
    public final ExpenseService expenseService;

    /**
     * Конструктор CommandBotProcessor.
     *
     * @param telegramBotController сервис для взаимодействия с ботом.
     * @param userService           сервис для взаимодействия с данными пользователя.
     * @param balanceService        сервис для взаимодействия с балансом.
     * @param incomeService         сервис для взаимодействия с доходами.
     * @param expenseService        сервис для взаимодействия с расходами.
     */
    public CommandBotProcessor(TelegramBotController telegramBotController, UserService userService, BalanceService balanceService,
                               IncomeService incomeService, ExpenseService expenseService) {
        this.telegramBotController = telegramBotController;
        this.userService = userService;
        this.balanceService = balanceService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
    }


    /**
     * Обработка команды, полученной от пользователя
     *
     * @param message сообщение от пользователя, содержащее команду
     * @param chatId  идентификатор чата, в котором была отправлена команда
     */
    public void processCommand(String message, long chatId) {
        switch (message) {
            case START_COMMAND -> handleStart(chatId);
            case EXPENSES_COMMAND -> handleExpenses(chatId);
            case INCOME_COMMAND -> handleIncome(chatId);
            case HELP_COMMAND -> handleHelp(chatId);
            case BALANCE_COMMAND -> handleBalance(chatId);
            default -> handleOther(chatId);
        }
    }

    /**
     * Отправка текущего баланса пользователя
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleBalance(long chatId) {
        telegramBotController.sendMessage("Ваш баланс: " + balanceService.getBalance(chatId), chatId);
    }

    /**
     * Отправка справочной информации по командам и шаблонов добавления доходов/расходов
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleHelp(long chatId) {
        BotCommand[] arrayOfCommand = new Commands().getCommands();
        StringBuilder stringHelp = new StringBuilder("Справка по всем командам: \n");
        for (BotCommand botCommand : arrayOfCommand) {
            stringHelp.append(botCommand.command()).append(" - ")
                    .append(botCommand.description()).append("\n");
        }
        stringHelp.append("\nЧтобы добавить доход введите:\n+ <сумма> <описание>\n");
        stringHelp.append("\nЧтобы добавить расход введите:\n- <сумма> <описание>");
        telegramBotController.sendMessage(stringHelp.toString(), chatId);
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
        telegramBotController.sendMessage(sb.toString(), chatId);
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
        telegramBotController.sendMessage(sb.toString(), chatId);
    }

    /**
     * Начало взаимодействия с ботом, отправка приветственного сообщения
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleStart(long chatId) {
        userService.openChat(chatId);
        telegramBotController.sendMessage("Давайте начнём", chatId);
    }

    /**
     * Уведомление пользователя о неизвестной команде
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void handleOther(long chatId) {
        telegramBotController.sendMessage("Неизвестная команда", chatId);
    }

}
