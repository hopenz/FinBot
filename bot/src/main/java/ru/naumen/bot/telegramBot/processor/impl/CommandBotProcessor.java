package ru.naumen.bot.telegramBot.processor.impl;

import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.telegramBot.command.Commands;
import ru.naumen.bot.telegramBot.processor.BotProcessor;
import ru.naumen.bot.telegramBot.service.*;

import java.util.List;

import static ru.naumen.bot.telegramBot.command.Commands.*;

/**
 * Класс CommandBotProcessor реализует интерфейс {@link BotProcessor} и обрабатывает команды, отправленные пользователем в Telegram.
 * В зависимости от команды выполняются различные действия, такие как обработка команды "/start", получение доходов и расходов.
 */
@Component
public class CommandBotProcessor implements BotProcessor {

    /**
     * Сервис для взаимодействия с Telegram ботом.
     */
    private final BotService botService;

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
     * Конструктор CommandBotProcessor. Инициализирует процессор с сервисом {@link BotService}.
     *
     * @param botService     сервис для взаимодействия с ботом.
     * @param userService    сервис для взаимодействия с данными пользователя.
     * @param balanceService сервис для взаимодействия с балансом.
     * @param incomeService  сервис для взаимодействия с доходами.
     * @param expenseService сервис для взаимодействия с расходами.
     */
    public CommandBotProcessor(BotService botService, UserService userService, BalanceService balanceService,
                               IncomeService incomeService, ExpenseService expenseService) {
        this.botService = botService;
        this.userService = userService;
        this.balanceService = balanceService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
    }

    /**
     * Обрабатывает обновление от Telegram и выполняет соответствующую команду.
     * Команды обрабатываются с помощью метода switch-case по тексту команды.
     *
     * @param update обновление от Telegram, содержащее команду.
     */
    @Override
    public void process(Update update) {
        switch (update.message().text()) {
            case START_COMMAND -> handleStart(update);
            case EXPENSES_COMMAND -> handleExpenses(update);
            case INCOME_COMMAND -> handleIncome(update);
            case HELP_COMMAND -> handleHelp(update);
            case BALANCE_COMMAND -> handleBalance(update);
            default -> handleOther(update);
        }
    }

    /**
     * Обрабатывает команду "/balance" и отправляет текующий баланс пользователя
     */
    private void handleBalance(Update update) {
        botService.sendMessage("Ваш баланс: " + balanceService.getBalance(update), update);
    }

    /**
     * Обрабатывает команду "/help" и отправляет пользователю справку по командам
     *
     * @param update обновление от Telegram
     */
    private void handleHelp(Update update) {
        BotCommand[] arrayOfCommand = new Commands().getCommands();
        StringBuilder stringHelp = new StringBuilder("Справка по всем командам: \n");
        for (BotCommand botCommand : arrayOfCommand) {
            stringHelp.append(botCommand.command()).append(" - ")
                    .append(botCommand.description()).append("\n");
        }
        stringHelp.append("\nЧтобы добавить доход введите:\n+ <сумма> <описание>\n");
        stringHelp.append("\nЧтобы добавить расход введите:\n- <сумма> <описание>");
        botService.sendMessage(stringHelp.toString(), update);
    }

    /**
     * Обрабатывает команду "/income" и отправляет пользователю список доходов.
     *
     * @param update обновление от Telegram.
     */
    private void handleIncome(Update update) {
        List<Income> incomeList = incomeService.getIncomes(update);
        StringBuilder sb = new StringBuilder();
        sb.append("Ваши доходы:\n");
        for (Income income : incomeList) {
            sb.append(income.amount()).append(" - ").append(income.description()).append("\n");
        }
        botService.sendMessage(sb.toString(), update);
    }

    /**
     * Обрабатывает команду "/expenses" и отправляет пользователю список расходов.
     *
     * @param update обновление от Telegram.
     */
    private void handleExpenses(Update update) {
        List<Expense> expenseList = expenseService.getExpenses(update);
        StringBuilder sb = new StringBuilder();
        sb.append("Ваши расходы:\n");
        for (Expense expense : expenseList) {
            sb.append(expense.amount()).append(" - ").append(expense.description()).append("\n");
        }
        botService.sendMessage(sb.toString(), update);
    }

    /**
     * Обрабатывает команду "/start", открывая чат для пользователя и отправляя приветственное сообщение.
     *
     * @param update обновление от Telegram.
     */
    private void handleStart(Update update) {
        userService.openChat(update);
        botService.sendMessage("Давайте начнём", update);
    }

    /**
     * Обрабатывает неизвестные команды, отправляя сообщение о том, что команда не распознана.
     *
     * @param update обновление от Telegram.
     */
    private void handleOther(Update update) {
        botService.sendMessage("Неизвестная команда", update);
    }

}
