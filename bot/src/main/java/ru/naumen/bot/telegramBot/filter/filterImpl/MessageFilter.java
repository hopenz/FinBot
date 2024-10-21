package ru.naumen.bot.telegramBot.filter.filterImpl;

import com.pengrad.telegrambot.model.Update;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import ru.naumen.bot.telegramBot.filter.ABotFilter;
import ru.naumen.bot.telegramBot.processor.impl.MessageBotProcessor;

/**
 * Класс MessageFilter реализует фильтр для обработку сообщений,
 * отправленных пользователем.
 */
@Component
@Order(3)
public class MessageFilter extends ABotFilter {

    /**
     * Процессор для обработки сообщений, отправленных пользователем.
     */
    private final MessageBotProcessor botProcessor;

    /**
     * Конструктор MessageFilter.
     * Инициализирует фильтр с процессор сообщений.
     *
     * @param botProcessor процессор для обработки сообщений.
     */
    public MessageFilter(MessageBotProcessor botProcessor) {
        this.botProcessor = botProcessor;
    }

    /**
     * Выполняет фитрацию обновления.
     * Если обработка обновления не закончилась на предыдущих фильтрах,
     * то фильтр передает его на обработку процессору сообщений.
     * Затем передает управление следующему фильтру в цепочке, если он существует.
     *
     * @param update обновление от Telegram, которое нужно обработать.
     */
    @Override
    public void doFilter(Update update) {
        botProcessor.process(update);
        if (this.getNextFilter() != null) {
            this.getNextFilter().doFilter(update);
        }
    }
}
