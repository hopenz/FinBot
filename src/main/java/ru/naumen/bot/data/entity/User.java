package ru.naumen.bot.data.entity;

/**
 * Класс User представляет пользователя бота и содержит информацию о его настройках и состоянии чата.
 */
public class User {

    /**
     * Тип хранения данных пользователя.
     */
    private DataType dataType;

    /**
     * Идентификатор Google Sheet, для хранения данных в Google Sheet.
     */
    private String googleSheetId;

    /**
     * Текущее состояние чата с пользователем.
     */
    private ChatState chatState;

    /**
     * Конструктор для создания нового пользователя с указанным типом данных и состоянием чата.
     *
     * @param dataType  тип хранения данных пользователя.
     * @param chatState начальное состояние чата с пользователем.
     */
    public User(DataType dataType, ChatState chatState) {
        this.dataType = dataType;
        this.chatState = chatState;
    }

    /**
     * Возвращает тип хранения данных пользователя.
     *
     * @return тип хранения данных пользователя.
     */
    public DataType getDataType() {
        return dataType;
    }

    /**
     * Устанавливает тип хранения данных пользователя.
     *
     * @param dataType новый тип хранения данных пользователя.
     */
    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    /**
     * Возвращает идентификатор Google Sheet.
     *
     * @return идентификатор Google Sheet.
     */
    public String getGoogleSheetId() {
        return googleSheetId;
    }

    /**
     * Устанавливает идентификатор Google Sheet.
     *
     * @param googleSheetId идентификатор Google Sheet.
     */
    public void setGoogleSheetId(String googleSheetId) {
        this.googleSheetId = googleSheetId;
    }

    /**
     * Возвращает текущее состояние чата с пользователем.
     *
     * @return текущее состояние чата с пользователем.
     */
    public ChatState getChatState() {
        return chatState;
    }

    /**
     * Устанавливает текущее состояние чата с пользователем.
     *
     * @param chatState новое состояние чата с пользователем.
     */
    public void setChatState(ChatState chatState) {
        this.chatState = chatState;
    }
}
