package ru.naumen.bot.controller.telegram;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.naumen.bot.configuration.ApplicationConfig;
import ru.naumen.bot.controller.BotController;
import ru.naumen.bot.data.entity.AnswerMessage;
import ru.naumen.bot.data.entity.BotUpdate;
import ru.naumen.bot.interaction.Commands;
import ru.naumen.bot.processor.BotUpdateProcessor;

import java.util.Arrays;
import java.util.List;

/**
 * Класс {@link  TelegramBotController} отвечает за управление телеграмм-ботом и взаимодействие с ним.
 */
@Component
public class TelegramBotController implements BotController {

    /**
     * Экземпляр бота Телеграмм
     */
    private final TelegramBot telegramBot;

    /**
     * Обработчик обновлений, полученных ботом.
     */
    private final BotUpdateProcessor botUpdateProcessor;

    /**
     * Логгер для записи сообщений об ошибках
     */
    private final Logger logger = LoggerFactory.getLogger(TelegramBotController.class);

    /**
     * Конструктор {@link  TelegramBotController} инициализирует бота Telegram, устанавливает его команды
     * и настраивает слушатель обновлений для обработки входящих обновлений.
     *
     * @param applicationConfig  конфигурация, содержащая токен бота Telegram
     * @param botUpdateProcessor обработчик обновлений, полученных ботом
     */
    public TelegramBotController(ApplicationConfig applicationConfig, BotUpdateProcessor botUpdateProcessor) {
        this.telegramBot = new TelegramBot(applicationConfig.telegramToken());
        this.botUpdateProcessor = botUpdateProcessor;

        this.telegramBot.execute(new SetMyCommands(createCommandsMenu()));
        this.telegramBot.setUpdatesListener(updates -> {
            updates.forEach(this::processUpdate);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        }, exception -> {
            if (exception.response() != null) {
                logger.error(
                        "[Bot exception] :: Code: {}; Message: {}.",
                        exception.response().errorCode(),
                        exception.response().description(),
                        exception
                );
            } else {
                logger.error(
                        "[Unexpected exception] :: Message: {}.",
                        exception.getMessage(),
                        exception
                );
            }
        });
    }

    /**
     * Создание меню команд для бота
     *
     * @return массив команд
     */
    private BotCommand[] createCommandsMenu() {
        return Arrays.stream(Commands.values())
                .map(command -> new BotCommand(command.getCommand(), command.getDescription()))
                .toArray(BotCommand[]::new);
    }

    /**
     * Обрабатывает входящее обновление от пользователя, преобразуя его в объект {@link BotUpdate}.
     *
     * @param update обновление, содержащее информацию о действии пользователя
     */
    private void processUpdate(Update update) {
        Long chatId = null;
        String message = null;
        if (update.message() != null) {
            chatId = update.message().chat().id();
            message = update.message().text();
        }
        String callbackQueryData = null;
        String callbackQueryId = null;
        if (update.callbackQuery() != null) {
            callbackQueryData = update.callbackQuery().data();
            callbackQueryId = update.callbackQuery().id();
            chatId = update.callbackQuery().from().id();
        }
        BotUpdate botUpdate = new BotUpdate(chatId, message, callbackQueryData, callbackQueryId);

        List<AnswerMessage> answerMessages = botUpdateProcessor.processBotUpdate(botUpdate);
        sendMessages(answerMessages);
    }

    @Override
    public void sendMessage(String message, long chatId) {
        sendMessage(message, chatId, null);
    }

    @Override
    public void sendMessage(String message, long chatId, List<List<String>> buttons) {
        SendMessage sendMessage = new SendMessage(chatId, message);
        if (buttons == null) {
            telegramBot.execute(sendMessage);
            return;
        }
        InlineKeyboardButton[][] keyboardButtons = new InlineKeyboardButton[buttons.size()][];
        for (int i = 0; i < buttons.size(); i++) {
            keyboardButtons[i] = buttons.get(i).stream()
                    .map(button -> new InlineKeyboardButton(button).callbackData(button))
                    .toArray(InlineKeyboardButton[]::new);
        }
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(keyboardButtons);
        telegramBot.execute(sendMessage.replyMarkup(keyboardMarkup));
    }

    @Override
    public void sendPopUpMessage(String message, String callbackQueryId) {
        telegramBot.execute(new AnswerCallbackQuery(callbackQueryId).text(message));
    }

    @Override
    public void sendMessages(List<AnswerMessage> answerMessages) {
        answerMessages.forEach(answerMessage -> {
            if (answerMessage.getCallbackQueryId() != null) {
                sendPopUpMessage(answerMessage.getMessage(), answerMessage.getCallbackQueryId());
            } else if (answerMessage.getButtons() != null) {
                sendMessage(answerMessage.getMessage(), answerMessage.getChatId(), answerMessage.getButtons());
            } else {
                sendMessage(answerMessage.getMessage(), answerMessage.getChatId());
            }
        });
    }
}
