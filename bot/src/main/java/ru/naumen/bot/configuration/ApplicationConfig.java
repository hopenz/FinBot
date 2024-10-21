package ru.naumen.bot.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Класс ApplicationConfig представляет собой запись, используемую для
 * загрузки конфигурационных свойств приложения из файла настроек.
 *
 * <p>Аннотация {@link ConfigurationProperties} позволяет привязывать
 * свойства с префиксом "app" к полям этого класса. Свойство
 * {@code telegramToken} содержит токен для доступа к Telegram API.
 *
 * @param telegramToken токен для аутентификации в Telegram API.
 */
@ConfigurationProperties(prefix = "app", ignoreUnknownFields = false)
public record ApplicationConfig(
        String telegramToken
) {
}
