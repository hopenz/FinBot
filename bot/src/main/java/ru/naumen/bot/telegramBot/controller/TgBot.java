package ru.naumen.bot.telegramBot.controller;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.request.SetMyCommands;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.naumen.bot.configuration.ApplicationConfig;
import ru.naumen.bot.telegramBot.command.Commands;
import ru.naumen.bot.telegramBot.filter.chain.BotFilterChain;

/**
 * Класс TgBot является компонентом Spring, который расширяет функционал {@link TelegramBot}.
 * Он инициализируется с помощью токена, полученного из конфигурации приложения, и
 * обрабатывает входящие обновления от Telegram, используя цепочку фильтров.
 */
@Component
public class TgBot extends TelegramBot {

    private final ApplicationContext context;

    private final Logger logger = LoggerFactory.getLogger(TgBot.class);

    /**
     * Конструктор класса TgBot.
     *
     * @param context контекст приложения Spring, используется для получения конфигурации и других бинов.
     */
    public TgBot(ApplicationContext context) {
        super(context.getBean(ApplicationConfig.class).telegramToken());
        this.context = context;
    }

    /**
     * Метод, вызываемый после готовности приложения (событие {@link ApplicationReadyEvent}).
     * Устанавливает команды бота и регистрирует обработчик обновлений Telegram.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void run() {
        this.execute(new SetMyCommands(
                new Commands().getCommands()
        ));

        this.setUpdatesListener(updates -> {
            BotFilterChain botFilterChain = context.getBean(BotFilterChain.class);
            updates.forEach(botFilterChain::process);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, exception -> {
            if (exception.response() != null) {
                logger.error(
                        "[Bot exception] :: Code: {}; Message: {}.",
                        exception.response().errorCode(),
                        exception.response().description()
                );
            } else {
                logger.error("[Unexpected exception] :: Message: {}.", exception.getMessage());
            }
        });
    }

    /**
     * Метод, вызываемый при завершении работы приложения.
     * Останавливает работу бота и освобождает ресурсы.
     */
    @PreDestroy
    public void stop() {
        this.shutdown();
    }

}
