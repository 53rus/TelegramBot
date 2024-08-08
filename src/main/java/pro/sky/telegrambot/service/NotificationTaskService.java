package pro.sky.telegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.entity.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;

@Service
public class NotificationTaskService {
    private final NotificationTaskRepository repository;
    private final TelegramBot telegramBot;


    public NotificationTaskService(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.repository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }

    public void saveNotification(Matcher matcher, Long chatId) {
        NotificationTask task = new NotificationTask();

        LocalDateTime date = LocalDateTime.parse(matcher.group(1), DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        String notificationText = matcher.group(3);

        task.setChatId(chatId);
        task.setDateTime(date);
        task.setMessageText(notificationText);

        repository.save(task);
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void sendNotificationTask() {
        List<NotificationTask> notificationTaskList = repository.findAll();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        notificationTaskList.forEach(task -> {
            if (task.getDateTime().isBefore(now)) {
                SendMessage message = new SendMessage(task.getChatId(), task.getMessageText());
                SendResponse response = telegramBot.execute(message);
                repository.delete(task);
            }
        });
    }


}
