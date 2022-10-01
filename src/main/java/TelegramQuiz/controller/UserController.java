package TelegramQuiz.controller;

import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import TelegramQuiz.files.WorkWithFiles;
import TelegramQuiz.qrcode.GenerateQRCode;
import TelegramQuiz.service.CustomerService;
import TelegramQuiz.util.KeyboardButtonConstants;
import TelegramQuiz.util.KeyboardButtonUtil;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;


public class UserController {
    public static void handleMessage(User user, Message message) {

        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        } else if (message.hasContact()) {
            Contact contact = message.getContact();
            handleContact(user, message, contact);
        }
    }

    private static void handleContact(User user, Message message, Contact contact) {

        if(!contact.getPhoneNumber().matches("(\\+)?998\\d{9}")) return;

        String chatId = String.valueOf(message.getChatId());
        Customer customer = CustomerService.getCustomerByChatId(chatId);

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (customer == null) {
            customer = CustomerService.addCustomer(chatId, contact);

            try {
                File qrCodeFile = GenerateQRCode.getQRCodeFile(chatId, customer.getConfirmPassword());

                SendPhoto sendPhoto = new SendPhoto(chatId, new InputFile(qrCodeFile));
                sendPhoto.setReplyMarkup(new ReplyKeyboardRemove(true));
                ComponentContainer.MY_BOT.sendMsg(sendPhoto);
                return;
            } catch (WriterException | NotFoundException | IOException e) {
                sendMessage.setText("Qayta urunib ko'ring. ");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }else{
            sendMessage.setText("Menu: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }
    }

    private static void handleText(User user, Message message, String text) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (text.equals("/start")) {
            Customer customer = CustomerService.getCustomerByChatId(chatId);

            if (customer == null) {
                sendMessage.setText("Assalomu alaykum!");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getContactMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            }else{
                sendMessage.setText("Menu");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }
        else if (text.equals(KeyboardButtonConstants.CHOOSE_SUBJECT)){
            sendMessage.setText("Please Choice subject");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getChoiceSubjectMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        }
            else {
            Customer customer = CustomerService.getCustomerByChatId(chatId);
            if (customer == null) {
                sendMessage.setText("Salom");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getContactMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                if (!customer.isActive()) {
                    if (text.equals(customer.getConfirmPassword())) {
                        customer.setActive(true);
                        WorkWithFiles.writeCustomerList();

                        sendMessage.setText("Menu");
                        sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                        ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    } else {
                        sendMessage.setText("Kod noto'g'ri");
                        ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    }
                } else {

                }
            }
        }
    }

    public static void handleCallback(User user, Message message, String data) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

    }
}
