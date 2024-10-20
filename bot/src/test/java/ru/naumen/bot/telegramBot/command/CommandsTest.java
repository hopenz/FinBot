package ru.naumen.bot.telegramBot.command;

import com.pengrad.telegrambot.model.BotCommand;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommandsTest {

    @Test
    void testGetCommands() {
        Commands commands = new Commands();
        BotCommand[] expectedCommands = new BotCommand[]{
                new BotCommand("/start", "Начать работу с ботом"),
                new BotCommand("/expenses", "Получить расходы"),
                new BotCommand("/income", "Показать доходы"),
                new BotCommand("/help", "Справка по командам"),
                new BotCommand("/balance", "Текущий баланс")
        };

        BotCommand[] actualCommands = commands.getCommands();

        assertThat(expectedCommands).isEqualTo(actualCommands);
    }
}
