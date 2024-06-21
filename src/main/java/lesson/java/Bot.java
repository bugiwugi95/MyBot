package lesson.java;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.PhotoMessage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static utils.PhotoMessage.processingImage;

public class Bot extends TelegramLongPollingBot {


    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        String mesText = update.getMessage().getText();
        String chatId = message.getChatId().toString();
        try {
            ArrayList<String> photoPaths = new ArrayList<>(PhotoMessage.savePhotos(getFilesByMessage(message), getBotToken()));
            for (String path : photoPaths) {
                    processingImage(path);
                    execute(preparePhotoMessage(path, chatId));

            }
            sendMsg(update.getMessage().getChatId().toString(),mesText);
        } catch (IOException | TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }

    public synchronized void setButtons(SendMessage sendMessage) {
        // Создаем клавиуатуру
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        sendMessage.setReplyMarkup(replyKeyboardMarkup);
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);

        // Создаем список строк клавиатуры
        List<KeyboardRow> keyboard = new ArrayList<>();

        // Первая строчка клавиатуры
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        // Добавляем кнопки в первую строчку клавиатуры
        keyboardFirstRow.add(new KeyboardButton("“Привет”"));

        // Вторая строчка клавиатуры
        KeyboardRow keyboardSecondRow = new KeyboardRow();
        // Добавляем кнопки во вторую строчку клавиатуры
        keyboardSecondRow.add(new KeyboardButton("“Помощь”"));

        // Добавляем все строчки клавиатуры в список
        keyboard.add(keyboardFirstRow);
        keyboard.add(keyboardSecondRow);
        // и устанваливаем этот список нашей клавиатуре
        replyKeyboardMarkup.setKeyboard(keyboard);
    }

    public  void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException();
        }
    }

    private static SendPhoto preparePhotoMessage(String fileNameImage, String chatId) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        InputFile inputFile = new InputFile();
        inputFile.setMedia(new File(fileNameImage));
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setCaption("This dog");
        return sendPhoto;
    }

    private List<org.telegram.telegrambots.meta.api.objects.File> getFilesByMessage(Message message) {
        List<PhotoSize> photoSizes = message.getPhoto();
        ArrayList<org.telegram.telegrambots.meta.api.objects.File> files = new ArrayList<>();
        for (PhotoSize photoSize : photoSizes) {
            final String fileId = photoSize.getFileId();
            try {
                files.add(sendApiMethod(new GetFile(fileId)));

            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        }
        return files;
    }


    @Override
    public String getBotUsername() {
        return "magk95_bot";
    }

    @Override
    public String getBotToken() {
        return "7259093033:AAG-sIuZ-7DcP-crun47CnDU7DW9MGNrQLo";
    }


}