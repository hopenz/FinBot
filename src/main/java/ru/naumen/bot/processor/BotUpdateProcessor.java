package ru.naumen.bot.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.BotUpdate;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.exception.DaoException;
import ru.naumen.bot.handler.callback.CallbackHandler;
import ru.naumen.bot.handler.command.CommandHandler;
import ru.naumen.bot.handler.message.MessageHandler;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Класс для обработки обновлений, полученных ботом.
 */
@Component
public class BotUpdateProcessor {

    /**
     * Сервис для работы с пользовательскими данными
     */
    private final UserService userService;

    /**
     * Хранилище обработчиков команд, связанных с текстовыми командами
     */
    private final Map<String, CommandHandler> commandHandlers = new HashMap<>();

    /**
     * Хранилище обработчиков сообщений, связанных с состоянием чатов
     */
    private final Map<ChatState, MessageHandler> messageHandlers = new HashMap<>();

    /**
     * Хранилище обработчиков callback-запросов, связанных с состоянием чатов
     */
    private final Map<ChatState, CallbackHandler> callbackHandlers = new HashMap<>();

    /**
     * Логгер для записи сообщений об ошибках
     */
    private final Logger logger = LoggerFactory.getLogger(BotUpdateProcessor.class);

    /**
     * Конструктор для инициализации процессора.
     *
     * @param userService      сервис для работы с пользователями
     * @param commandHandlers  список обработчиков команд
     * @param callbackHandlers список обработчиков callback-запросов
     * @param messageHandlers  список обработчиков сообщений
     */
    public BotUpdateProcessor(UserService userService, List<CommandHandler> commandHandlers,
                              List<CallbackHandler> callbackHandlers, List<MessageHandler> messageHandlers) {
        this.userService = userService;
        this.commandHandlers.putAll(commandHandlers.stream()
                .collect(Collectors.toMap(CommandHandler::getCommand, handler -> handler)));
        this.callbackHandlers.putAll(callbackHandlers.stream()
                .collect(Collectors.toMap(CallbackHandler::getChatState, handler -> handler)));
        this.messageHandlers.putAll(messageHandlers.stream()
                .collect(Collectors.toMap(MessageHandler::getChatState, handler -> handler)));
    }

    /**
     * Основной метод обработки обновлений.
     * Определяет тип обновления и вызывает соответствующий метод обработки.
     *
     * @param botUpdate объект, содержащий данные обновления
     * @return список ответных сообщений для пользователя
     */
    public List<AnswerMessage> processBotUpdate(BotUpdate botUpdate) {
        long chatId = botUpdate.chatId();

        if (!userService.isChatOpened(chatId)
                && (!botUpdate.isTextMessage()
                || !botUpdate.message().equals(Commands.START_COMMAND.getCommand()))) {
            return List.of(new AnswerMessage(
                    "Чтобы начать работу, нажмите " + Commands.START_COMMAND.getCommand(), chatId));
        }

        try {
            if (botUpdate.isCallbackQuery()) {
                return processCallback(botUpdate.callbackData(), botUpdate.callbackId(), chatId);
            }
            if (botUpdate.isTextMessage()) {
                String message = botUpdate.message();
                if (message.startsWith("/")) {
                    return processCommand(message, chatId);
                } else {
                    return processMessage(message, chatId);
                }
            }
        } catch (GoogleSheetsException exception) {
            return handleGoogleSheetsException(exception, chatId);
        } catch (DaoException exception) {
            logger.error(exception.getMessage(), exception);
            return List.of(new AnswerMessage("Произошла какая-то ошибка", chatId));
        }

        return List.of(new AnswerMessage("Неподдерживаемое сообщение", chatId));
    }

    /**
     * Обработка callback-запроса.
     *
     * @param callbackData данные callback-запроса
     * @param callbackId   идентификатор callback-запроса
     * @param chatId       идентификатор чата
     * @return список сообщений-ответов
     * @throws DaoException если возникает ошибка работы с данными
     */
    private List<AnswerMessage> processCallback(String callbackData, String callbackId,
                                                long chatId) throws DaoException {
        CallbackHandler callbackHandler = callbackHandlers.get(userService.getUserState(chatId));
        List<AnswerMessage> answerMessages = new ArrayList<>(List.of(
                new AnswerMessage("Вы выбрали '" + callbackData + "'", callbackId)));
        if (callbackHandler != null) {
            answerMessages.addAll(callbackHandler.handleCallback(callbackData, callbackId, chatId));
        }
        return answerMessages;
    }

    /**
     * Обработка текстового сообщения.
     *
     * @param message текст сообщения
     * @param chatId  идентификатор чата
     * @return список сообщений-ответов
     * @throws DaoException если возникает ошибка работы с данными
     */
    private List<AnswerMessage> processMessage(String message, long chatId) throws DaoException {
        MessageHandler messageHandler = messageHandlers.getOrDefault(userService.getUserState(chatId),
                messageHandlers.get(ChatState.NOTHING_WAITING));
        return messageHandler.handleMessage(message, chatId);
    }

    /**
     * Обработка команды.
     *
     * @param message текст команды
     * @param chatId  идентификатор чата
     * @return список сообщений-ответов
     * @throws DaoException если возникает ошибка работы с данными
     */
    private List<AnswerMessage> processCommand(String message, long chatId) throws DaoException {
        CommandHandler commandHandler = commandHandlers.get(message);
        if (commandHandler != null) {
            return commandHandler.handleCommand(message, chatId);
        }
        return List.of(new AnswerMessage("Неизвестная команда", chatId));
    }

    /**
     * Обработка исключения, связанного с Google Sheets.
     *
     * @param exception исключение GoogleSheetsException
     * @param chatId    идентификатор чата
     * @return список сообщений-ответов для пользователя
     */
    private List<AnswerMessage> handleGoogleSheetsException(GoogleSheetsException exception, long chatId) {
        logger.error("[GoogleSheetsException exception] :: Message: {}.", exception.getMessage(), exception);
        userService.setDataType(chatId, DataType.IN_MEMORY);
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        return List.of(
                new AnswerMessage("""
                        С вашей гугл-таблицей что-то не так \uD83E\uDEE3
                        Проверьте, что ваша таблица соответствует требованиям:
                        1. Она должна быть открыта
                        2. У бота должны быть права редактора
                        3. В таблице должно быть 3 листа "Общая информация", "Расходы", "Доходы"
                        """, chatId),
                new AnswerMessage("Вы будете переведены на режим работы с данными в памяти." +
                        " Данные, который вы заполняли в гугл-таблице, будут утеряны.", chatId)
        );
    }
}
