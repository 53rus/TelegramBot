package pro.sky.telegrambot.exception;

public class InvalidRequestFormatException extends RuntimeException {

    public InvalidRequestFormatException() {
        super("Неверный формат сообщения." +
                "Введите строку формата dd.MM.yyyy HH:mm Текст напоминания");
    }
}
