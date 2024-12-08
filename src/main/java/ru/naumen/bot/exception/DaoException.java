package ru.naumen.bot.exception;

/**
 * Исключение, представляющее ошибку, связанную с взаимодействием с БД
 */
public class DaoException extends Exception {

    /**
     * Конструктор, инициализирующий исключение с причиной.
     *
     * @param exception исключение, которое стало причиной возникновения {@code DaoException}.
     */
    public DaoException(Exception exception) {
        super(exception);
    }
}
