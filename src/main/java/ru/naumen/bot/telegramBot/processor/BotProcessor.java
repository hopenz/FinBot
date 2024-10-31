package ru.naumen.bot.telegramBot.processor;

import com.pengrad.telegrambot.model.Update;

/**
 * Интерфейс BotProcessor определяет контракт для обработки обновлений Telegram.
 * Любой класс, реализующий этот интерфейс, должен предоставлять логику обработки входящих сообщений и событий.
 */
public interface BotProcessor {

    /**
     * Обрабатывает обновление от Telegram.
     * Метод, который должен реализовать конкретную логику обработки обновлений.
     *
     * @param update объект {@link Update}, представляющий обновление от Telegram.
     */
    void process(Update update);

}

