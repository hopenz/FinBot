package ru.naumen.bot.telegramBot.processor;

import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.ExpenseService;
import ru.naumen.bot.telegramBot.service.IncomeService;

import static ru.naumen.bot.telegramBot.command.Commands.HELP_COMMAND;

@Component
public class MessageBotProcessor{

    private static final String INCOMES_PATTERN = "^\\+\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";
    private static final String EXPENSES_PATTERN = "^\\-\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";

    /**
     * Сервис для взаимодействия с Telegram ботом.
     */
    private final TelegramBotController botController;

    /**
     * Сервис для взаимодействия с доходом.
     */
    private final IncomeService incomeService;

    /**
     * Сервис для взаимодействия с расходом.
     */
    private final ExpenseService expenseService;

    /**
     * Конструктор MessageBotProcessor.
     *
     * @param botController     сервис для взаимодействия с ботом.
     * @param incomeService  сервис для взаимодействия с доходом.
     * @param expenseService сервис для взаимодействия с расходом.
     */
    public MessageBotProcessor(TelegramBotController botController, IncomeService incomeService,
                               ExpenseService expenseService) {
        this.botController = botController;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
    }

    public void processMessage(String message, long chatId) {
        if (message.matches(EXPENSES_PATTERN)) {
            expenseService.addExpense(message, chatId);
            botController.sendMessage("Расход успешно добавлен!", chatId);
        } else if (message.matches(INCOMES_PATTERN)) {
            incomeService.addIncome(message, chatId);
            botController.sendMessage("Доход успешно добавлен!", chatId);
        } else {
            botController.sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                    + HELP_COMMAND, chatId);
        }
    }

}
