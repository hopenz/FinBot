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

    /**
     * Конструктор, инициализирующий исключение с причиной и сообщением .
     *
     * @param message   сообщение об ошибке, которое описывает причину возникновения исключения
     * @param exception исходное исключение, которое вызвало данное исключение.
     */
    public DaoException(String message, Exception exception) {
        super(message, exception);
    }
}
