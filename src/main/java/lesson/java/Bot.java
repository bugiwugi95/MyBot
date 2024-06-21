package lesson.java;

import functions.FilterOperation;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.ImageUtils;
import utils.RgbMater;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

public class Bot extends TelegramLongPollingBot {
    String localNameFile = "ssssss";
    @Override
    public String getBotUsername() {
        return "magk95_bot";
    }

    @Override
    public String getBotToken() {
        return "7259093033:AAG-sIuZ-7DcP-crun47CnDU7DW9MGNrQLo";
    }

    @Override
    public void onUpdateReceived(Update update) {

        String message_text = update.getMessage().getText();
        String chat_id = String.valueOf(update.getMessage().getChatId());
        if (update.hasMessage() && update.getMessage().hasText()) {
            zerkloText(message_text, chat_id);
        } else if (update.hasMessage() && update.getMessage().hasPhoto()) {
            PhotoSize photo = update.getMessage().getPhoto().get(0);
            String fileId = photo.getFileId();
            org.telegram.telegrambots.meta.api.objects.File  file;
            try {
                file = sendApiMethod(new GetFile(fileId));
                String imageUrl = "https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath();
                saveImage(imageUrl, localNameFile);

            } catch (TelegramApiException | IOException e) {
                throw new RuntimeException(e);
            }

            try {
                processingImage(localNameFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            SendPhoto msg = new SendPhoto();
            InputFile inputFile = new InputFile();
            inputFile.setMedia(new File(localNameFile));
            msg.setChatId(chat_id);
            msg.setPhoto(inputFile);

            try {
                execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }





    private void zerkloText(String message_text, String chat_id) {
        SendMessage message = new SendMessage();
        message.setChatId(chat_id);
        message.setText(message_text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void saveImage(String url, String fileName) throws IOException {
        URL urlModel = new URL(url);
        InputStream inputStream = urlModel.openStream();
        OutputStream outputStream = new FileOutputStream(fileName);
        byte[] b = new byte[2048];
        int length;
        while ((length = inputStream.read(b)) != -1) {
            outputStream.write(b, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }


    public static void processingImage(String fileName) throws IOException {
        BufferedImage image = ImageUtils.getImage(fileName);
        RgbMater rgbMater = new RgbMater(image);
        rgbMater.changeImage(FilterOperation::onlyRed);
        ImageUtils.saveImage(rgbMater.getImage(), fileName);
    }


}

