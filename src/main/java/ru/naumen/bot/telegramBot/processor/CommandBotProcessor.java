package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.BotCommand;
import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.telegramBot.command.Commands;
import ru.naumen.bot.telegramBot.controller.TelegramBotController;
import ru.naumen.bot.telegramBot.service.*;

import java.util.List;

import static ru.naumen.bot.telegramBot.command.Commands.*;


@Component
public class CommandBotProcessor {

//TODO
    private final TelegramBotController botService;

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
     * @param botService     сервис для взаимодействия с ботом.
     * @param userService    сервис для взаимодействия с данными пользователя.
     * @param balanceService сервис для взаимодействия с балансом.
     * @param incomeService  сервис для взаимодействия с доходами.
     * @param expenseService сервис для взаимодействия с расходами.
     */
    public CommandBotProcessor(TelegramBotController botService, UserService userService, BalanceService balanceService,
                               IncomeService incomeService, ExpenseService expenseService) {
        this.botService = botService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
    }


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


    private void handleBalance(long chatId) {
        botService.sendMessage("Ваш баланс: " + balanceService.getBalance(chatId), chatId);
    }


    private void handleHelp(long chatId) {
        BotCommand[] arrayOfCommand = new Commands().getCommands();
        StringBuilder stringHelp = new StringBuilder("Справка по всем командам: \n");
        for (BotCommand botCommand : arrayOfCommand) {
            stringHelp.append(botCommand.command()).append(" - ")
                    .append(botCommand.description()).append("\n");
        }
        stringHelp.append("\nЧтобы добавить доход введите:\n+ <сумма> <описание>\n");
        stringHelp.append("\nЧтобы добавить расход введите:\n- <сумма> <описание>");
        botService.sendMessage(stringHelp.toString(), chatId);
    }


    private void handleIncome(long chatId) {
        List<Income> incomeList = incomeService.getIncomes(chatId);
        StringBuilder sb = new StringBuilder();
        sb.append("Ваши доходы:\n");
        for (Income income : incomeList) {
            sb.append(income.amount()).append(" - ").append(income.description()).append("\n");
        }
        botService.sendMessage(sb.toString(), chatId);
    }

    private void handleExpenses(long chatId) {
        List<Expense> expenseList = expenseService.getExpenses(chatId);
        StringBuilder sb = new StringBuilder();
        sb.append("Ваши расходы:\n");
        for (Expense expense : expenseList) {
            sb.append(expense.amount()).append(" - ").append(expense.description()).append("\n");
        }
        botService.sendMessage(sb.toString(), chatId);
    }


    private void handleStart(long chatId) {
        userService.openChat(chatId);
        botService.sendMessage("Давайте начнём", chatId);
    }


    private void handleOther(long chatId) {
        botService.sendMessage("Неизвестная команда", chatId);
    }

}
