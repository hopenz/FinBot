package ru.naumen.bot.data.enums;

/**
 * Перечисление DataType определяет типы хранилищ данных,
 * используемых в приложении.
 */
public enum DataType {

    /**
     * Данные хранятся в памяти.
     */
    IN_MEMORY,

    /**
     * Данные хранятся в Google Sheet.
     */
    IN_GOOGLE_SHEET
}
