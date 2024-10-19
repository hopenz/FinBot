package ru.naumen.bot.telegramBot.command;

import com.pengrad.telegrambot.model.BotCommand;

/**
 * Класс Commands предоставляет список команд, доступных для Telegram-бота.
 * Каждая команда начинается с префикса "/", и предназначена для выполнения определенных действий.
 */
public class Commands {

    /**
     * Префикс для всех команд бота.
     */
    public static final String COMMAND_PREFIX = "/";

    /**
     * Команда для начала работы с ботом.
     */
    public static final String START_COMMAND = COMMAND_PREFIX + "start";

    /**
     * Команда для получения информации о расходах.
     */
    public static final String EXPENSES_COMMAND = COMMAND_PREFIX + "expenses";

    /**
     * Команда для получения информации о доходах.
     */
    public static final String INCOME_COMMAND = COMMAND_PREFIX + "income";

    /**
     * Команда для получения информации про команды
     */
    public static final String HELP_COMMAND = COMMAND_PREFIX + "help";

    /** Команда для получения текущего баланса пользователя
     *
     */
    public static final String BALANCE_COMMAND = COMMAND_PREFIX + "balance";

    /**
     * Метод возвращает массив доступных команд для бота.
     *
     * @return массив команд, каждая из которых описывает действие и текстовое описание.
     */
    public  BotCommand[] getCommands() {
        return new BotCommand[] {
                new BotCommand(
                        START_COMMAND,
                        "Начать работу с ботом"
                ),

                new BotCommand(
                        EXPENSES_COMMAND,
                        "Получить расходы"
                ),

                new BotCommand(
                        INCOME_COMMAND,
                        "Показать доходы"
                ),
                new BotCommand(
                        HELP_COMMAND,
                        "Справка по командам"
                ),
                new BotCommand(
                        BALANCE_COMMAND,
                        "Текущий баланс"
                )
        };
    }
}
