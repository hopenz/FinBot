package ru.naumen.bot.processor;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.naumen.bot.command.Commands;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.service.ExpenseService;
import ru.naumen.bot.service.IncomeService;

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
     * Сервис для взаимодействия с ботом.
     */
    private final BotController botController;

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
     * @param botController  сервис для взаимодействия с ботом.
     * @param incomeService  сервис для взаимодействия с доходами.
     * @param expenseService сервис для взаимодействия с расходами.
     */
    public MessageBotProcessor(@Lazy BotController botController, IncomeService incomeService,
                               ExpenseService expenseService) {
        this.botController = botController;
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
            botController.sendMessage("Расход успешно добавлен!", chatId);
        } else if (message.matches(INCOMES_PATTERN)) {
            incomeService.addIncome(message, chatId);
            botController.sendMessage("Доход успешно добавлен!", chatId);
        } else {
            botController.sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                    + Commands.HELP_COMMAND.getCommand(), chatId);
        }
    }

}
