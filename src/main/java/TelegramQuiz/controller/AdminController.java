package TelegramQuiz.controller;

import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import TelegramQuiz.entity.Question;
import TelegramQuiz.files.WorkWithFiles;
import TelegramQuiz.util.InlineButtonConstants;
import TelegramQuiz.util.KeyboardButtonConstants;
import TelegramQuiz.util.KeyboardButtonUtil;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;


public class AdminController {
    public static void handleMessage(User user, Message message) {

        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        }
    }


    private static void handleText(User user, Message message, String text) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (text.equals("/start")) {
            sendMessage.setText("Hello "  +user.getUserName());
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.GET_USERS_LIST_EXCEL)) {
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(WorkWithFiles.generateCustomerExcelFile(Database.customerList)));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);
        } else if (text.equals(KeyboardButtonConstants.GET_ALL_HISTORY_PDF)) {
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(WorkWithFiles.generateAllHistoryPdfFile(Database.historyList)));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);
        }

    }




    public static void handleCallback(User user, Message message, String data) {
//        String chatId = String.valueOf(message.getChatId());
//
//        SendMessage sendMessage = new SendMessage();
//        sendMessage.setChatId(chatId);
//
//        if (data.equals(InlineButtonConstants.CONFIRM_CALL_BACK)) {
////            Announce announce = ComponentContainer.announceMap.get(chatId);
////            CandidateService.addCandidate(announce);
//
//            sendMessage.setText(announce.getFullName() + " muvaffaqiyatli saqlandi.");
//        } else if (data.equals(InlineButtonConstants.CANCEL_CALL_BACK)) {
//            Announce announce = ComponentContainer.announceMap.get(chatId);
//            sendMessage.setText(announce.getFullName() + " saqlanmadi.");
//        }
//
//        ComponentContainer.statusMap.remove(chatId);
//        ComponentContainer.announceMap.remove(chatId);
//
//        ComponentContainer.MY_BOT.sendMsg(sendMessage);
//
//        DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
//        ComponentContainer.MY_BOT.sendMsg(deleteMessage);
    }
}

