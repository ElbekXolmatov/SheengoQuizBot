package TelegramQuiz.controller;

import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import TelegramQuiz.entity.History;
import TelegramQuiz.entity.Question;
import TelegramQuiz.files.WorkWithFiles;
import TelegramQuiz.qrcode.GenerateQRCode;
import TelegramQuiz.service.CustomerService;
import TelegramQuiz.util.InlineButtonConstants;
import TelegramQuiz.util.InlineKeyboardUtil;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class UserController {

    public static List<Question> collect;
    static boolean doTest;
    static int countQuestion;
    static int currentQuestion;
    static int countTrueAnswer;
    static int idHistory;

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

        if (!contact.getPhoneNumber().matches("(\\+)?998\\d{9}")) return;

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
        } else {
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

            } else {
                sendMessage.setText("Menu");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }

        //<//teginilmasin!
        else if (text.equals(KeyboardButtonConstants.BACK_TO_MAIN_MENU)) {
            if (doTest) {
                DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
                ComponentContainer.MY_BOT.sendMsg(deleteMessage);
                DeleteMessage deleteMessage1 = new DeleteMessage(chatId, message.getMessageId() - 1);
                ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
                SendMessage sendMessage1 = new SendMessage(chatId, "Menu:");
                doTest = false;
                sendMessage.setText("Test tugadi" + "\nNatijangiz->" + countTrueAnswer + "/" + countQuestion);
                sendMessage1.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.MY_BOT.sendMsg(sendMessage1);
                History history = new History(idHistory,chatId,collect.get(0).getSubject(),countQuestion,countTrueAnswer,LocalDateTime.now().toString());
                Database.historyList.add(history);
                WorkWithFiles.writeHistoryList();
                currentQuestion = 0;
                countTrueAnswer = 0;
            } else {
                sendMessage.setText("Menu:");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }
        else if (text.equals(KeyboardButtonConstants.BACK_TO_SUBJECT_MENU)) {
            if (Database.subjectsList.size() == 0) {
                sendMessage.setText("Hozircha fanlar yo'q");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                if (doTest) {
                    DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
                    ComponentContainer.MY_BOT.sendMsg(deleteMessage);
                    DeleteMessage deleteMessage1 = new DeleteMessage(chatId, message.getMessageId() - 1);
                    ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(chatId);
                    doTest = false;
                    sendMessage1.setText("Test tugadi" + "\nNatijangiz->" + countTrueAnswer + "/" + countQuestion);
                    ComponentContainer.MY_BOT.sendMsg(sendMessage1);
                    sendMessage.setText("Please Choice subject");
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getChoiceSubjectMenu());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    History history = new History(idHistory,chatId,collect.get(0).getSubject(),countQuestion,countTrueAnswer,LocalDateTime.now().toString());
                    Database.historyList.add(history);
                    WorkWithFiles.writeHistoryList();
                    currentQuestion = 0;
                    countTrueAnswer = 0;

                } else {
                    sendMessage.setText("Please Choice subject");
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getChoiceSubjectMenu());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    doTest = false;
                }
            }
        }
        else if (text.equals(KeyboardButtonConstants.CHOOSE_SUBJECT)) {
            if (Database.subjectsList.size() == 0) {
                sendMessage.setText("Hozircha fanlar yo'q");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                sendMessage.setText("Please Choice subject");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getChoiceSubjectMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }
        else if (Objects.requireNonNull(Database.subjectsList.stream().
                filter(subject -> subject.getTitle().equals(text)).
                findFirst().
                orElse(null)).getTitle().equals(text)) {
            collect = Database.questionsList.stream().filter(question -> question.getSubject().equals(text)).collect(Collectors.toList());
            System.out.println(collect);
            if (collect.size() == 0) {
                sendMessage.setText("Hozircha bu fandan savollar mavjud emas!");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.backToSubjectsMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else {
                SendMessage sendMessage1 = new SendMessage();
                sendMessage1.setChatId(chatId);
                sendMessage1.setText("Please select count of questions!");
                sendMessage.setText("Welcome to " + text + " test");
                sendMessage1.setReplyMarkup(InlineKeyboardUtil.getCountOfQuestions());
                sendMessage.setReplyMarkup(KeyboardButtonUtil.backToSubjectsMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.MY_BOT.sendMsg(sendMessage1);
//            }
            }

        }
        //teginilmasin>//

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
        //<//Teginilmasin
        if (data.equals(InlineButtonConstants.FIVE_CALLBACK) || data.equals(InlineButtonConstants.TEN_CALLBACK) ||
                data.equals(InlineButtonConstants.TWENTY_CALLBACK) || data.equals(InlineButtonConstants.THIRTY_CALLBACK)
        ) {
            String[] split = data.split("/");
            countQuestion = Integer.parseInt(split[0]);
            Collections.shuffle(collect);
            for (int i = 0; i < countQuestion; i++) {
                Collections.shuffle(collect.get(i).getAnswer());
            }
            if (collect.size() >= countQuestion) {
                doTest = true;
                idHistory++;
            } else {
                sendMessage.setText("Buncha savol yoq");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                System.out.println(collect.size());
            }
        }
        else if (data.equals(InlineButtonConstants.ALL_CALLBACK)) {
            countQuestion = collect.size();
            Collections.shuffle(collect);
            for (int i = 0; i < countQuestion; i++) {
                Collections.shuffle(collect.get(i).getAnswer());
            }
            idHistory++;
            doTest = true;
        }
        if (doTest) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
            if (currentQuestion != 0) {
                StringBuilder sb = new StringBuilder();
                sb.append(collect.get(currentQuestion - 1).getTitle() + "\n");
                sb.append("Javoblar\n");
                for (int i = 0; i < collect.get(currentQuestion - 1).getAnswer().size(); i++) {
                    sb.append(collect.get(currentQuestion - 1).getAnswer().get(i) + "\n");
                }
                sb.append("To'gri javob-> " + collect.get(currentQuestion - 1).getCorrectAns() + "\n");
                if (collect.get(currentQuestion - 1).getCorrectAns().
                        equals(collect.get(currentQuestion - 1).getAnswer().get(Integer.parseInt(data)))) {
                    countTrueAnswer++;
                    sb.append("Barakalla " + CustomerService.getCustomerByChatId(chatId).getFirstName() +
                            " To'gri Javobni topdingiz \uD83E\uDD73" + "\n");
                    sb.append("Togri javoblaringiz soni->" + countTrueAnswer);
                } else {
                    sb.append("Afsuski javobingiz xato" + CustomerService.getCustomerByChatId(chatId).
                            getFirstName() +
                            " \uD83D\uDE14" + "\n");
                    sb.append("Togri javoblaringiz soni->" + countTrueAnswer);
                }
                sendMessage.setText(String.valueOf(sb));
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            if (currentQuestion == countQuestion) {
                doTest = false;
                sendMessage.setText("Test tugadi" + "\nNatijangiz->" + countTrueAnswer + "/" + countQuestion);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                History history = new History(idHistory,chatId,collect.get(0).getSubject(),countQuestion,countTrueAnswer,LocalDateTime.now().toString());
                Database.historyList.add(history);
                WorkWithFiles.writeHistoryList();
                currentQuestion = 0;
                countTrueAnswer = 0;

            }
            else {
                int size = collect.get(currentQuestion).getAnswer().size();
                sendMessage.setText(collect.get(currentQuestion).getTitle());
                sendMessage.setReplyMarkup(InlineKeyboardUtil.getAnswers(currentQuestion, size));
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                currentQuestion++;
            }
        }
        //Teginilmasin>>


    }


}
