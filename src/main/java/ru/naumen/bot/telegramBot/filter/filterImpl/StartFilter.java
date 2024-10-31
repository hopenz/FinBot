package ru.naumen.bot.telegramBot.filter.filterImpl;

import com.pengrad.telegrambot.model.Update;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.filter.ABotFilter;
import ru.naumen.bot.telegramBot.service.BotService;
import ru.naumen.bot.telegramBot.service.UserService;

import static ru.naumen.bot.telegramBot.command.Commands.START_COMMAND;

/**
 * Класс StartFilter реализует фильтр для обработки команды "/start".
 * Если чат с пользователем еще не открыт, отправляется сообщение с указанием начать работу с помощью команды "/start".
 */
@Component
@Order(1)
public class StartFilter extends ABotFilter {

    /**
     * Сервис для взаимодействия с ботом.
     */
    private final BotService botService;

    /**
     * Сервис для взаимодействия с данными.
     */
    private final UserService userService;

    /**
     * Конструктор StartFilter.
     * Инициализирует фильтр с сервисом {@link BotService}.
     *
     * @param botService  сервис для взаимодействия с ботом.
     * @param userService сервис для взаимодействия с данными.
     */
    public StartFilter(BotService botService, UserService userService) {
        this.botService = botService;
        this.userService = userService;
    }

    /**
     * Выполняет фильтрацию обновления.
     * Проверяет, была ли команда "/start" или открыт ли чат. Если ни одно из условий не выполнено,
     * отправляет пользователю сообщение с инструкцией о начале работы.
     *
     * @param update обновление от Telegram, которое нужно обработать.
     */
    @Override
    public void doFilter(Update update) {
        if (!START_COMMAND.equals(update.message().text()) && !userService.isChatOpened(update)) {
            botService.sendMessage(
                    "Чтобы начать работу, нажмите " + START_COMMAND,
                    update
            );
        } else if (this.getNextFilter() != null) {
            this.getNextFilter().doFilter(update);
        }
    }

}
