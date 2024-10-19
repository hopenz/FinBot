package ru.naumen.bot.telegramBot.service.processor.impl;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.telegramBot.service.BotService;
import ru.naumen.bot.telegramBot.service.DataService;
import ru.naumen.bot.telegramBot.service.processor.BotProcessor;

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
     * Сервис для взаимодействия с данными.
     */
    private final DataService dataService;

    /**
     * Конструктор CommandBotProcessor. Инициализирует процессор с сервисом {@link BotService}.
     *
     * @param botService  сервис для взаимодействия с ботом.
     * @param dataService сервис для взаимодействия с данными.
     */
    public CommandBotProcessor(BotService botService, DataService dataService) {
        this.botService = botService;
        this.dataService = dataService;
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
            default -> handleOther(update);
        }
    }

    /**
     * Обрабатывает команду "/income" и отправляет пользователю список доходов.
     *
     * @param update обновление от Telegram.
     */
    private void handleIncome(Update update) {
        List<Income> incomeList = dataService.getIncomes(update);
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
        List<Expense> expenseList = dataService.getExpenses(update);
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
        dataService.openChat(update);
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
