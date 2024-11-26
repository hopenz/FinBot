package ru.naumen.bot.processor;

import org.springframework.stereotype.Component;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.exception.GoogleSheetsException;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.interaction.keyboards.TypeDBKeyboard;
import ru.naumen.bot.processor.exception.handler.GoogleSheetsExceptionHandler;
import ru.naumen.bot.service.DatabaseService;
import ru.naumen.bot.service.UserService;

/**
 * Класс CallbackQueryProcessor обрабатывает callback-запросы от пользователя,
 * отправляемые при нажатии на кнопки inline-клавиатуры.
 */
@Component
public class CallbackQueryProcessor {

    /**
     * Контроллер, отвечающий за управление ботом и взаимодействие с ним.
     */
    private final BotController botController;

    /**
     * Сервис для работы с данными о пользователях.
     */
    private final UserService userService;

    /**
     * Сервис для управления базами данных.
     */
    private final DatabaseService databaseService;

    /**
     * Обработчик исключений Google Sheets.
     */
    private final GoogleSheetsExceptionHandler exceptionHandler;

    /**
     * Конструктор CallbackQueryProcessor.
     *
     * @param botController    контроллер, отвечающий за управление ботом и взаимодействие с ним.
     * @param userService      сервис для работы с данными о пользователях.
     * @param databaseService  сервис для управления базами данных.
     * @param exceptionHandler обработчик исключений Google Sheets.
     */
    public CallbackQueryProcessor(BotController botController, UserService userService,
                                  DatabaseService databaseService, GoogleSheetsExceptionHandler exceptionHandler) {
        this.botController = botController;
        this.userService = userService;
        this.databaseService = databaseService;
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Обрабатывает callback-запрос, исходя из текущего состояния чата с пользователем.
     *
     * @param data    значение нажатой кнопки в inline-клавиатуре.
     * @param chatId  идентификатор чата.
     * @param queryId идентификатор callback-запроса.
     */
    public void processCallbackQuery(String data, Long chatId, String queryId) {
        ChatState chatState = userService.getUserState(chatId);

        botController.sendPopUpMessage("Вы выбрали " + data, queryId);
        switch (chatState) {
            case WAITING_FOR_TYPE_DB -> processTypeDBAfterStartCommand(data, chatId);
            case WAITING_FOR_TYPE_DB_FOR_CHANGE_DB -> processTypeDBForChangeDB(data, chatId);
        }
    }

    /**
     * Обрабатывает выбор типа базы данных при смене базы данных.
     *
     * @param data   данные о выбранном типе базы данных.
     * @param chatId идентификатор чата.
     */
    private void processTypeDBForChangeDB(String data, Long chatId) {
        DataType dataType = data.equals(TypeDBKeyboard.GOOGLE_SHEETS.getData())
                ? DataType.IN_GOOGLE_SHEET
                : DataType.IN_MEMORY;

        if (userService.getDataType(chatId).equals(dataType)) {
            botController.sendMessage("Вы уже используете этот способ хранения", chatId);
            return;
        }

        if (userService.getGoogleSheetId(chatId) == null) {
            requestGoogleSheetId(chatId);
            return;
        }

        try {
            databaseService.changeDB(chatId, dataType);
        } catch (GoogleSheetsException exception) {
            botController.sendMessage("Во время смены базы данных произошла ошибка", chatId);
            exceptionHandler.handleGoogleSheetsException(exception, chatId);
            return;
        }
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        botController.sendMessage("Теперь ваши данные хранятся в '" + data + "'!", chatId);
    }

    /**
     * Обрабатывает выбор типа базы данных для первоначальной установки.
     *
     * @param data   данные о выбранном типе базы данных.
     * @param chatId идентификатор чата.
     */
    private void processTypeDBAfterStartCommand(String data, Long chatId) {
        if (data.equals(TypeDBKeyboard.GOOGLE_SHEETS.getData())) {
            requestGoogleSheetId(chatId);
        } else {
            botController.sendMessage("Теперь ваши данные хранятся в оперативной памяти!", chatId);
            userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        }
        botController.sendMessage(
                "Помните, вы можете поменять способ хранения данных с помощью команды " +
                        Commands.CHANGE_DB_COMMAND.getCommand(), chatId);
    }

    /**
     * Запрашивает ссылку на Google Sheet для привязки
     *
     * @param chatId идентификатор чата.
     */
    private void requestGoogleSheetId(Long chatId) {
        userService.setUserState(chatId, ChatState.WAITING_FOR_GOOGLE_SHEET_LINK);
        botController.sendMessage("""
                Давайте создадим таблицу и привяжем к ней бота:
                1. Для создания таблицы перейдите по ссылке https://sheets.new/
                2. Добавьте finbotaccount@celtic-house-440906-m1.iam.gserviceaccount.com в качестве пользователя-редактор
                3. Пришлите мне ссылку на новую созданную таблицу (просто скопируйте и отправьте мне), остальное я сделаю сам
                """, chatId);
    }


}
