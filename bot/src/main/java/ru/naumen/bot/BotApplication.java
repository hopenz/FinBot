package ru.naumen.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.naumen.bot.configuration.ApplicationConfig;

/**
 * Главный класс приложения BotApplication, который является точкой входа в
 * приложение на основе Spring Boot.
 *
 * <p>Аннотация {@link EnableConfigurationProperties} позволяет использовать
 * свойства, определенные в классе {@link ApplicationConfig}, для
 * конфигурации приложения.
 */
@SpringBootApplication
@EnableConfigurationProperties(ApplicationConfig.class)
public class BotApplication {

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

}
