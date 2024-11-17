package ru.naumen.bot.advice;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.entity.ChatState;
import ru.naumen.bot.data.entity.DataType;
import ru.naumen.bot.exception.GoogleSheetsException;
import ru.naumen.bot.service.UserService;

/**
 * Обработчик исключений для контроллера бота
 */
@Component
public class BotControllerAdvice {

    /**
     * Контроллер для взаимодействия с ботом
     */
    private final BotController botController;

    /**
     * Сервис для управления данными пользователей
     */
    private final UserService userService;

    /**
     * Конструктор класса BotControllerAdvice
     *
     * @param botController контроллер для взаимодействия с ботом
     * @param userService   сервис для управления данными пользователей
     */
    public BotControllerAdvice(@Lazy BotController botController, UserService userService) {
        this.botController = botController;
        this.userService = userService;
    }

    /**
     * Обработка исключения GoogleSheetsException
     *
     * @param exception возникшее исключение
     */
    public void handleGoogleSheetsException(GoogleSheetsException exception) {
        botController.sendMessage(exception.getMessageForBot(), exception.getChatId());
        userService.setDataType(exception.getChatId(), DataType.IN_MEMORY);
        userService.setUserState(exception.getChatId(), ChatState.NOTHING_WAITING);
        botController.sendMessage("""
                        С вашей гугл-таблицей что-то не так \uD83E\uDEE3
                        Проверьте, что ваша таблица соответствует требованиям:
                        1. Она должна быть открыта
                        2. У бота должны быть права редактора
                        3. В таблице должно быть 3 листа "Общая информация", "Расходы", "Доходы"
                        """,
                exception.getChatId());

        botController.sendMessage("Вы будете переведены на режим работы с данными в памяти." +
                        " Данные, который вы заполняли в гугл-таблице, будут утеряны.",
                exception.getChatId());
    }
}
