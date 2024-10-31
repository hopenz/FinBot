package ru.naumen.bot.telegramBot.service;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.naumen.bot.telegramBot.controller.TgBot;

/**
 * Сервис BotService предоставляет основные методы для работы с Telegram ботом.
 */
@Service
public class BotService {

    /**
     * Инстанс бота для отправки запросов Telegram API.
     */
    private final TgBot bot;

    /**
     * Конструктор BotService. Инициализирует сервис с объектом бота {@link TgBot}.
     *
     * @param bot инстанс бота для взаимодействия с Telegram API.
     */
    @Autowired
    public BotService(TgBot bot) {
        this.bot = bot;
    }

    /**
     * Отправляет сообщение в чат Telegram.
     *
     * @param message текст сообщения для отправки.
     * @param update  обновление от Telegram, содержащее информацию о чате.
     */
    public void sendMessage(String message, Update update) {
        long chatId = update.message().chat().id();
        bot.execute(new SendMessage(chatId, message));
    }

}
