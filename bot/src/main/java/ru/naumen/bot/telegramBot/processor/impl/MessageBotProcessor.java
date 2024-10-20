package ru.naumen.bot.telegramBot.processor.impl;

import com.pengrad.telegrambot.model.Update;
import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.processor.BotProcessor;
import ru.naumen.bot.telegramBot.service.BotService;
import ru.naumen.bot.telegramBot.service.ExpenseService;
import ru.naumen.bot.telegramBot.service.IncomeService;

import static ru.naumen.bot.telegramBot.command.Commands.HELP_COMMAND;

/**
 * Класс MessageBotProcessor реализует интерфейс {@link BotProcessor} и обрабатывает сообщения,
 * отправленные пользователем в Telegram.
 * <p>
 * Отлавливаются сообщения, которые соответствуют паттерну сообщения добавления расходов или доходов.
 */
@Component
public class MessageBotProcessor implements BotProcessor {

    private static final String INCOMES_PATTERN = "^\\+\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";
    private static final String EXPENSES_PATTERN = "^\\-\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";


    /**
     * Сервис для взаимодействия с Telegram ботом.
     */
    private final BotService botService;

    /**
     * Сервис для взаимодействия с доходом.
     */
    private final IncomeService incomeService;

    /**
     * Сервис для взаимодействия с расходом.
     */
    private final ExpenseService expenseService;

    /**
     * Конструктор MessageBotProcessor. Инициализирует процессор с сервисом {@link BotService}.
     *
     * @param botService     сервис для взаимодействия с ботом.
     * @param incomeService  сервис для взаимодействия с доходом.
     * @param expenseService сервис для взаимодействия с расходом.
     */
    public MessageBotProcessor(BotService botService, IncomeService incomeService, ExpenseService expenseService) {
        this.botService = botService;
        this.incomeService = incomeService;
        this.expenseService = expenseService;
    }

    /**
     * Обрабатывает обновление от Telegram и выполняет соответствующее действие.
     * Сообщение обрабатывается с помощью проверки соответствия строки регулярному выражению.
     *
     * @param update объект {@link Update}, представляющий обновление от Telegram.
     */
    @Override
    public void process(Update update) {
        String text = update.message().text();
        if (text.matches(EXPENSES_PATTERN)) {
            addExpense(text, update);
        } else if (text.matches(INCOMES_PATTERN)) {
            addIncome(text, update);
        } else {
            botService.sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                    + HELP_COMMAND, update);
        }
    }

    /**
     * Добавляет доход и отправляет соответствующее собщение пользователю.
     *
     * @param income сообщение пользователя.
     * @param update обноление от Telegram.
     */
    private void addIncome(String income, Update update) {
        incomeService.addIncome(income, update);
        botService.sendMessage("Доход успешно добавлен!", update);
    }

    /**
     * Добавляет расход и отправляет соответствующее собщение пользователю.
     *
     * @param expense сообщение пользователя.
     * @param update  обноление от Telegram.
     */
    private void addExpense(String expense, Update update) {
        expenseService.addExpense(expense, update);
        botService.sendMessage("Расход успешно добавлен!", update);
    }
}
