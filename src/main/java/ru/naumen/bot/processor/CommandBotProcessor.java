package ru.naumen.bot.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.Expense;
import ru.naumen.bot.data.entity.Income;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.processor.exception.handler.GoogleSheetsExceptionHandler;
import ru.naumen.bot.service.FinanceDataService;
import ru.naumen.bot.service.UserService;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
     * Сервис для взаимодействия с данными о финансах.
     */
    private final FinanceDataService financeDataService;

    /**
     * Сервис для обработки исключений при работе с Google Sheets.
     */
    private final GoogleSheetsExceptionHandler exceptionHandler;

    /**
     * Логгер для записи сообщений об ошибках
     */
    private final Logger logger = LoggerFactory.getLogger(CommandBotProcessor.class);

    /**
     * Map для хранения команд, где ключ это текст команды, а значение соответствующий экземпляр {@link Commands}
     */
    private final Map<String, Commands> commandsMap = new Hashtable<>();

    /**
     * Конструктор CommandBotProcessor.
     *
     * @param botController      сервис для взаимодействия с ботом.
     * @param userService        сервис для взаимодействия с данными пользователя.
     * @param financeDataService сервис для взаимодействия с данными о финансах.
     * @param exceptionHandler   сервис для обработки исключений Google Sheets.
     */
    public CommandBotProcessor(BotController botController, UserService userService,
                               FinanceDataService financeDataService, GoogleSheetsExceptionHandler exceptionHandler) {
        this.botController = botController;
        this.financeDataService = financeDataService;
        this.userService = userService;
        this.exceptionHandler = exceptionHandler;

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
        if (!userService.isChatOpened(chatId)) {
            if (!message.equals(Commands.START_COMMAND.getCommand())) {
                botController.sendMessage("Чтобы начать работу, нажмите " + Commands.START_COMMAND.getCommand(),
                        chatId);
                return;
            }
        } else {
            userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        }

        if (commandsMap.containsKey(message)) {
            executeCommand(commandsMap.get(message), chatId);
        } else {
            processOtherCommand(chatId);
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
            case Commands.START_COMMAND -> processStartCommand(chatId);
            case Commands.EXPENSES_COMMAND -> processExpensesCommand(chatId);
            case Commands.INCOMES_COMMAND -> processIncomesCommand(chatId);
            case Commands.HELP_COMMAND -> processHelpCommand(chatId);
            case Commands.BALANCE_COMMAND -> processBalanceCommand(chatId);
            case Commands.CHANGE_DB_COMMAND -> processChangeDbCommand(chatId);
        }
    }

    /**
     * Команда для изменения базы данных.
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void processChangeDbCommand(long chatId) {
        userService.setUserState(chatId, ChatState.WAITING_FOR_TYPE_DB_FOR_CHANGE_DB);
        botController.sendMessage("Выберите базу данных", chatId,
                Arrays.stream(TypeDBKeyboard.values())
                        .map(TypeDBKeyboard::getData)
                        .toList());
    }

    /**
     * Отправка текущего баланса пользователя
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void processBalanceCommand(long chatId) {
        Double balance;
        try {
            balance = financeDataService.getBalance(chatId);
        } catch (GoogleSheetsException exception) {
            botController.sendMessage("Во время отправки баланса произошла ошибка", chatId);
            exceptionHandler.handleGoogleSheetsException(exception, chatId);
            return;
        } catch (DaoException exception) {
            logger.error(exception.getMessage(), exception);
            return;
        }

        botController.sendMessage("Ваш баланс: " + balance, chatId);
    }

    /**
     * Отправка справочной информации по командам и шаблонов добавления доходов/расходов
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void processHelpCommand(long chatId) {
        Commands[] arrayOfCommand = Commands.values();
        StringBuilder stringHelp = new StringBuilder("Справка по всем командам: \n");
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
    private void processIncomesCommand(long chatId) {
        List<Income> incomeList;
        try {
            incomeList = financeDataService.getIncomes(chatId);
        } catch (GoogleSheetsException exception) {
            botController.sendMessage("Во время отправки доходов произошла ошибка", chatId);
            exceptionHandler.handleGoogleSheetsException(exception, chatId);
            return;
        } catch (DaoException exception) {
            logger.error(exception.getMessage(), exception);
            return;
        }
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
    private void processExpensesCommand(long chatId) {
        List<Expense> expenseList;
        try {
            expenseList = financeDataService.getExpenses(chatId);
        } catch (GoogleSheetsException exception) {
            botController.sendMessage("Во время отправки расходов произошла ошибка", chatId);
            exceptionHandler.handleGoogleSheetsException(exception, chatId);
            return;
        } catch (DaoException exception) {
            logger.error(exception.getMessage(), exception);
            return;
        }
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
    private void processStartCommand(long chatId) {
        if (userService.isChatOpened(chatId)) {
            botController.sendMessage(
                    "Ещё раз здравствуйте, чтобы ознакомиться с командами - напишите " +
                            Commands.HELP_COMMAND.getCommand(), chatId);
        } else {
            userService.openChat(chatId);
            botController.sendMessage(
                    "Здравствуйте! Как вы хотите хранить данные?",
                    chatId, Arrays.stream(TypeDBKeyboard.values())
                            .map(TypeDBKeyboard::getData)
                            .toList());
        }
    }

    /**
     * Уведомление пользователя о неизвестной команде
     *
     * @param chatId идентификатор чата, в котором была отправлена команда
     */
    private void processOtherCommand(long chatId) {
        botController.sendMessage("Неизвестная команда", chatId);
    }

}