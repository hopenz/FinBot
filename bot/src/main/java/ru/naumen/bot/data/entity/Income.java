package ru.naumen.bot.data.entity;

import java.time.LocalDate;

/**
 * Запись Income представляет доход пользователя.
 * Содержит описание, сумму и дату дохода.
 *
 * @param description описание дохода.
 * @param amount      сумма дохода.
 * @param date        дата получения дохода.
 */
public record Income(String description, Double amount, LocalDate date) {
}
