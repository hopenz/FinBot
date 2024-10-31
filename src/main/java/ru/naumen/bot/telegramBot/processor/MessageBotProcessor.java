package ru.naumen.bot.telegramBot.processor;

import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.ExpenseService;
import ru.naumen.bot.telegramBot.service.IncomeService;

import static ru.naumen.bot.telegramBot.command.Commands.HELP_COMMAND;

/**
 * Класс  {@link  MessageBotProcessor}, в котором происходит
 * обработка сообщений, полученных от пользователей
 */
@Component
public class MessageBotProcessor {

    /**
     * Шаблон регулярного выражения для распознания сообщений о доходах
     */
    private static final String INCOMES_PATTERN = "^\\+\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";

    /**
     * Шаблон регулярного выражения для распознания сообщений о расходах
     */
    private static final String EXPENSES_PATTERN = "^\\-\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";

    /**
     * Сервис для взаимодействия с Telegram ботом.
     */
    private final TelegramBotController telegramBotController;

    /**
     * Сервис для взаимодействия с доходами.
     */
    private final IncomeService incomeService;

    /**
     * Сервис для взаимодействия с расходами.
     */
    private final ExpenseService expenseService;

    /**
     * Конструктор MessageBotProcessor.
     *
     * @param telegramBotController сервис для взаимодействия с ботом.
     * @param incomeService         сервис для взаимодействия с доходами.
     * @param expenseService        сервис для взаимодействия с расходами.
     */
    public MessageBotProcessor(TelegramBotController telegramBotController, IncomeService incomeService,
                               ExpenseService expenseService) {
        this.telegramBotController = telegramBotController;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
    }

    /**
     * Обработка сообщения от пользователя
     *
     * @param message сообщение от пользователя
     * @param chatId  идентификатор чата, в котором было отправлено сообщение
     */
    public void processMessage(String message, long chatId) {
        if (message.matches(EXPENSES_PATTERN)) {
            expenseService.addExpense(message, chatId);
            telegramBotController.sendMessage("Расход успешно добавлен!", chatId);
        } else if (message.matches(INCOMES_PATTERN)) {
            incomeService.addIncome(message, chatId);
            telegramBotController.sendMessage("Доход успешно добавлен!", chatId);
        } else {
            telegramBotController.sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                    + HELP_COMMAND, chatId);
        }
    }

}
