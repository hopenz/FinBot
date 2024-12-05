package ru.naumen.bot.interaction;

/**
 * Перечисление предоставляет список команд, доступных для бота.
 * Каждая команда имеет описание и текст команды.
 */
public enum Commands {
    /**
     * Команда для начала работы с ботом.
     */
    START_COMMAND("/start", "Начать работу с ботом"),

    /**
     * Команда для получения информации о расходах.
     */
    EXPENSES_COMMAND("/expenses", "Получить информацию о расходах"),

    /**
     * Команда для получения информации о доходах.
     */
    INCOMES_COMMAND("/incomes", "Получить информацию о доходах"),

    /**
     * Команда для получения информации о доступных командах.
     */
    HELP_COMMAND("/help", "Справка по командам"),

    /**
     * Команда для получения текущего баланса пользователя.
     */
    BALANCE_COMMAND("/balance", "Текущий баланс"),

    /**
     * Команда для смены базы данных.
     */
    CHANGE_DB_COMMAND("/changedb", "Сменить базу данных"),

    /**
     * Команда для вывода сумм расходов по категориям за месяц
     */
    ALL_CAT_OF_EXPENSES("/all_cat_of_exp", "Вывод сумм расходов по категориям за месяц"),

    /**
     * Команда для вывода расходов по категории за месяц
     */
    EXPENSES_BY_CAT("/expenses_by_cat", "Вывод расходов по категории за месяц"),
    ;

    /**
     * Текст команды.
     */
    private final String command;

    /**
     * Описание команды.
     */
    private final String description;

    /**
     * Конструктор для инициализации команды и ее описания.
     *
     * @param command     Текст команды.
     * @param description Описание команды.
     */
    Commands(String command, String description) {
        this.command = command;
        this.description = description;
    }

    /**
     * Возвращает текст команды.
     *
     * @return Текст команды.
     */
    public String getCommand() {
        return command;
    }

    /**
     * Возвращает описание команды.
     *
     * @return Описание команды.
     */
    public String getDescription() {
        return description;
    }
}
