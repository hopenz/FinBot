package ru.naumen.bot.data.entity;

/**
 * Перечисление ChatState, определяет возможные состояния чата.
 * Каждое состояние представляет текущую стадию взаимодействия пользователя с ботом.
 */
public enum ChatState {

    /**
     * Состояние, когда от пользователя не ожидается никаких определенных действий.
     */
    NOTHING_WAITING,

    /**
     * Состояние ожидания выбора типа базы данных пользователем.
     */
    WAITING_FOR_TYPE_DB,

    /**
     * Состояние ожидания выбора типа базы данных пользователем для смены текущей базы данных.
     */
    WAITING_FOR_TYPE_DB_FOR_CHANGE_DB,

    /**
     * Состояние ожидания ссылки на Google Sheet.
     */
    WAITING_FOR_GOOGLE_SHEET_LINK

}
