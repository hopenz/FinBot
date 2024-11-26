package ru.naumen.bot.processor.exception.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.dao.googleSheets.exception.GoogleSheetsException;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.service.UserService;

/**
 * Обработчик исключений возникших во время работы с Google Sheets
 */
@Component
public class GoogleSheetsExceptionHandler {

    /**
     * Контроллер для взаимодействия с ботом
     */
    private final BotController botController;

    /**
     * Сервис для управления данными пользователей
     */
    private final UserService userService;

    /**
     * Логгер для записи сообщений об ошибках
     */
    private final Logger logger = LoggerFactory.getLogger(GoogleSheetsExceptionHandler.class);

    /**
     * Конструктор класса BotControllerAdvice
     *
     * @param botController контроллер для взаимодействия с ботом
     * @param userService   сервис для управления данными пользователей
     */
    public GoogleSheetsExceptionHandler(BotController botController, UserService userService) {
        this.botController = botController;
        this.userService = userService;
    }

    /**
     * Обработка исключения GoogleSheetsException
     *
     * @param exception возникшее исключение
     * @param chatId    идентификатор чата
     */
    public void handleGoogleSheetsException(GoogleSheetsException exception, long chatId) {
        logger.error("[GoogleSheetsException exception] :: Message: {}.", exception.getMessage(), exception);
        userService.setDataType(chatId, DataType.IN_MEMORY);
        userService.setUserState(chatId, ChatState.NOTHING_WAITING);
        botController.sendMessage("""
                        С вашей гугл-таблицей что-то не так \uD83E\uDEE3
                        Проверьте, что ваша таблица соответствует требованиям:
                        1. Она должна быть открыта
                        2. У бота должны быть права редактора
                        3. В таблице должно быть 3 листа "Общая информация", "Расходы", "Доходы"
                        """,
                chatId);

        botController.sendMessage("Вы будете переведены на режим работы с данными в памяти." +
                        " Данные, который вы заполняли в гугл-таблице, будут утеряны.",
                chatId);
    }
}
