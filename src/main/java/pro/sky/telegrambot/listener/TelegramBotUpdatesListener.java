package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.exception.InvalidRequestFormatException;
import pro.sky.telegrambot.service.NotificationTaskService;


import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final NotificationTaskService service;

    public TelegramBotUpdatesListener(NotificationTaskService service) {
        this.service = service;
    }

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);
            String messageText = update.message().text();
            Long chatId = update.message().chat().id();
            Pattern pattern = Pattern.compile("(\\d{2}\\.\\d{2}\\.\\d{4}\\s\\d{2}:\\d{2})(\\s+)(.+)");
            Matcher matcher = pattern.matcher(messageText);

                if (messageText.equals("/start")) {

                    SendMessage message = new SendMessage(chatId, "Привет, это телеграмм бот, который сохраняет ваши задачи, а также напоминает о них");
                    SendResponse response = telegramBot.execute(message);
                }
                else if (matcher.matches()) {
                    service.saveNotification(matcher, chatId);
                    logger.info("Задача сохранена");

                    SendMessage message = new SendMessage(chatId, "Задача сохранен");
                    SendResponse response = telegramBot.execute(message);
                }else {
                    logger.error("Ошибка, неверный формат запроса");

                    SendMessage message = new SendMessage(chatId, "Неверный формат сообщения." +
                            " Введите строку формата dd.MM.yyyy HH:mm Текст напоминания");
                    SendResponse response = telegramBot.execute(message);
                }


        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }


}
