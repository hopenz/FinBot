package ru.naumen.bot.exception;

public class DaoException extends Exception {

    public DaoException(Exception exception) {
        super(exception);
    }
}
