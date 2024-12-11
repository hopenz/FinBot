package ru.naumen.bot.data.dao.googleSheets.exception;

import ru.naumen.bot.exception.DaoException;

/**
 * Исключение, возникающее при ошибках, связанных с работой с Google Sheets
 */
public class GoogleSheetsException extends DaoException {

    /**
     * Конструктор исключения
     *
     * @param exception исключение, которое произошло при работе с Google Sheets
     */
    public GoogleSheetsException(String message, Exception exception) {
        super(message, exception);
    }

}
