package ru.naumen.bot.telegramBot.filter.filterImpl;

import com.pengrad.telegrambot.model.Update;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.filter.ABotFilter;
import ru.naumen.bot.telegramBot.service.processor.impl.CommandBotProcessor;

import static ru.naumen.bot.telegramBot.command.Commands.COMMAND_PREFIX;

/**
 * Класс CommandFilter реализует фильтр для обработки команд, отправленных пользователем.
 * Если сообщение начинается с префикса команды ("/"), оно передается на обработку в {@link CommandBotProcessor}.
 */
@Component
@Order(2)
public class CommandFilter extends ABotFilter {

    /**
     * Процессор для обработки команд, отправленных пользователем.
     */
    private final CommandBotProcessor botProcessor;

    /**
     * Конструктор CommandFilter.
     * Инициализирует фильтр с процессором команд.
     *
     * @param botProcessor процессор для обработки команд.
     */
    public CommandFilter(CommandBotProcessor botProcessor) {
        this.botProcessor = botProcessor;
    }

    /**
     * Выполняет фильтрацию обновления.
     * Если сообщение начинается с префикса команды ("/"), передает его на обработку процессору команд.
     * Затем передает управление следующему фильтру в цепочке, если он существует.
     *
     * @param update обновление от Telegram, которое нужно обработать.
     */
    @Override
    public void doFilter(Update update) {
        if (update.message().text().startsWith(COMMAND_PREFIX)) {
            botProcessor.process(update);
        }else if (this.getNextFilter() != null) {
            this.getNextFilter().doFilter(update);
        }
    }

}
