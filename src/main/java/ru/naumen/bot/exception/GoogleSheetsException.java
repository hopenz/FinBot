package ru.naumen.bot.exception;

/**
 * Исключение, возникающее при ошибках, связанных с работой с Google Sheets
 */
public class GoogleSheetsException extends RuntimeException {

    /**
     * Конструктор исключения
     *
     * @param exception исключение, которое произошло при работе с Google Sheets
     */
    public GoogleSheetsException(Exception exception) {
        super(exception);
    }

}
