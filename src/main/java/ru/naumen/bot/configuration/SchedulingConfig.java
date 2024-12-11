package ru.naumen.bot.configuration;


import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Конфигурационный класс для настройки расписания удаления суммы расходов за день.
 */
@Configuration
@EnableScheduling // Оставили аннотацию здесь, так как она должна быть в классе конфигурации (исходя из документации).
public class SchedulingConfig {

}
