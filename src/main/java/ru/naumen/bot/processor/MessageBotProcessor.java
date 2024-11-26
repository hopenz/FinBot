package ru.naumen.bot.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.processor.exception.handler.GoogleSheetsExceptionHandler;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.FinanceDataService;
import ru.naumen.bot.service.UserService;

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
    private static final String EXPENSES_PATTERN = "^-\\s\\d{1,8}(\\.\\d{1,2})?\\s.{0,100}$";

    /**
     * Шаблон регулярного выражения для распознания ссылок на Google Sheet
     */
    private static final String GOOGLE_SHEET_LINK_PATTERN = "^https://docs\\.google\\.com/spreadsheets/d/.{1,200}$";

    /**
     * Сервис для взаимодействия с ботом.
     */
    private final BotController botController;

    /**
     * Сервис для взаимодействия с данными о финансах.
     */
    private final FinanceDataService financeDataService;

    /**
     * Сервис для взаимодействия с данными о пользователях.
     */
    private final UserService userService;

    /**
     * Сервис для взаимодействия с базами данных.
     */
    private final DatabaseService databaseService;

    /**
     * Сервис для обработки исключений при работе с Google Sheets.
     */
    private final GoogleSheetsExceptionHandler exceptionHandler;

    /**
     * Логгер для записи сообщений об ошибках
     */
    private final Logger logger = LoggerFactory.getLogger(MessageBotProcessor.class);


    /**
     * Конструктор MessageBotProcessor.
     *
     * @param botController      сервис для взаимодействия с ботом.
     * @param financeDataService сервис для взаимодействия с данными о финансах.
     * @param userService        сервис для взаимодействия с данными о пользователях.
     * @param databaseService    сервис для взаимодействия с базами данных.
     * @param exceptionHandler   сервис для обработки исключений Google Sheets.
     */
    public MessageBotProcessor(BotController botController, FinanceDataService financeDataService, UserService userService,
                               DatabaseService databaseService, GoogleSheetsExceptionHandler exceptionHandler) {
        this.botController = botController;
        this.financeDataService = financeDataService;
        this.userService = userService;
        this.databaseService = databaseService;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Обработка сообщения от пользователя
     *
     * @param message сообщение от пользователя
     * @param chatId  идентификатор чата, в котором было отправлено сообщение
     */
    public void processMessage(String message, long chatId) {
        if (!userService.isChatOpened(chatId)) {
            botController.sendMessage("Чтобы начать работу, нажмите " +
                    Commands.START_COMMAND.getCommand(), chatId);
            return;
        }

        ChatState chatState = userService.getUserState(chatId);
        if (chatState.equals(ChatState.WAITING_FOR_GOOGLE_SHEET_LINK)) {
            processMessageWithGoogleSheetLink(message, chatId);
        } else {
            processSimpleMessage(message, chatId);
        }

        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
    }

    /**
     * Обработка сообщений со ссылкой на Google Sheet
     *
     * @param message сообщение, содержащее ссылку
     * @param chatId  идентификатор чата
     */
    private void processMessageWithGoogleSheetLink(String message, long chatId) {
        if (message.matches(GOOGLE_SHEET_LINK_PATTERN)) {
            userService.setGoogleSheetId(chatId, message);
            botController.sendMessage("Создаю страницы документа, подготавливаю к работе …\n", chatId);
            try {
                databaseService.changeDB(chatId, DataType.IN_GOOGLE_SHEET);
            } catch (GoogleSheetsException exception) {
                botController.sendMessage("Во время инициализации таблицы произошла ошибка", chatId);
                exceptionHandler.handleGoogleSheetsException(exception, chatId);
                return;
            } catch (DaoException exception) {
                logger.error(exception.getMessage(), exception);
                return;
            }
            botController.sendMessage("Бот готов к работе!", chatId);
        } else {
            botController.sendMessage("Данная ссылка не верна", chatId);
        }
    }

    /**
     * Обработка простых сообщений
     *
     * @param message сообщение, отправленное пользователем
     * @param chatId  идентификатор чата
     */
    private void processSimpleMessage(String message, long chatId) {
        if (message.matches(EXPENSES_PATTERN)) {
            try {
                financeDataService.addExpense(message, chatId);
            } catch (GoogleSheetsException exception) {
                botController.sendMessage("Во время добавления расхода произошла ошибка", chatId);
                exceptionHandler.handleGoogleSheetsException(exception, chatId);
                return;
            } catch (DaoException exception) {
                logger.error(exception.getMessage(), exception);
                return;
            }
            botController.sendMessage("Расход успешно добавлен!", chatId);
        } else if (message.matches(INCOMES_PATTERN)) {
            try {
                financeDataService.addIncome(message, chatId);
            } catch (GoogleSheetsException exception) {
                botController.sendMessage("Во время добавления дохода произошла ошибка", chatId);
                exceptionHandler.handleGoogleSheetsException(exception, chatId);
                return;
            } catch (DaoException exception) {
                logger.error(exception.getMessage(), exception);
                return;
            }
            botController.sendMessage("Доход успешно добавлен!", chatId);
        } else {
            botController.sendMessage("Я вас не понял.\nЧтобы ознакомиться с командами - напишите "
                    + Commands.HELP_COMMAND.getCommand(), chatId);
        }
    }
}

