package TelegramQuiz.Bot;


import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.controller.AdminController;
import TelegramQuiz.controller.UserController;
import TelegramQuiz.db.Database;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.swing.text.Document;

public class QuizBot extends TelegramLongPollingBot {
    @Override
    public String getBotToken() {
        return ComponentContainer.BOT_TOKEN;
    }

    @Override
    public String getBotUsername() {
        return ComponentContainer.BOT_USERNAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            User user = message.getFrom();

            String chatId = String.valueOf(message.getChatId());

            if (ComponentContainer.ADMIN_CHAT_IDS.contains(chatId)) {
                AdminController.handleMessage(user, message);
            } else {
                UserController.handleMessage(user, message);
            }

        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            Message message = callbackQuery.getMessage();
            User user = callbackQuery.getFrom();
            String data = callbackQuery.getData();

            String chatId = String.valueOf(message.getChatId());

            if (ComponentContainer.ADMIN_CHAT_IDS.contains(chatId)) {
                AdminController.handleCallback(user, message, data);
            } else {
                UserController.handleCallback(user, message, data);
            }
        }
    }

    public Message sendMsg(Object obj) {
        try {
            if (obj instanceof SendMessage) {
              return   execute((SendMessage) obj);
            } else if (obj instanceof DeleteMessage) {
                execute((DeleteMessage) obj);
            } else if (obj instanceof EditMessageText) {
                execute((EditMessageText) obj);
            } else if (obj instanceof SendPhoto) {
                Message message = execute((SendPhoto) obj);
                if(!ComponentContainer.ADMIN_CHAT_IDS.contains(message.getChatId().toString())){
                    Database.messageList.add(message);
                }
            } else if (obj instanceof SendDocument) {
                execute((SendDocument) obj);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        return null;
    }
//    public void sendMsg(SendDocument sendDocument) {
//        try {
//            execute(sendDocument);
//        } catch (TelegramApiException e) {
//            e.printStackTrace();
//        }
//    }
}
