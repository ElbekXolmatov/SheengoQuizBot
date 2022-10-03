package TelegramQuiz.controller;

import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import TelegramQuiz.entity.History;
import TelegramQuiz.entity.Question;
import TelegramQuiz.entity.History;
import TelegramQuiz.files.WorkWithFiles;
import TelegramQuiz.qrcode.GenerateQRCode;
import TelegramQuiz.service.CustomerService;
import TelegramQuiz.util.InlineButtonConstants;
import TelegramQuiz.util.InlineKeyboardUtil;
import TelegramQuiz.util.KeyboardButtonConstants;
import TelegramQuiz.util.KeyboardButtonUtil;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import javassist.bytecode.analysis.Executor;
import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class UserController {

    public static List<Question> collect;
    static boolean doTest;
    static int countQuestion;
    static int currentQuestion;
    static int countTrueAnswer;
    static int idHistory;
    static LocalDateTime now ;

    static LocalTime beginLocalTime;


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
        } else {
            Customer customer = CustomerService.getCustomerByChatId(chatId);
            if (customer == null) {
                sendMessage.setText("Assalomu alaykum!");
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
                    //Manguberdi Part Started
                    if (text.equals(KeyboardButtonConstants.BACK_TO_MAIN_MENU)) {
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
                            History history = new History(idHistory, chatId, collect.get(0).getSubject(), countQuestion, countTrueAnswer, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
                            Database.historyList.add(history);
                            WorkWithFiles.writeHistoryList();
                            currentQuestion = 0;
                            countTrueAnswer = 0;
                        } else {
                            sendMessage.setText("Menu:");
                            sendMessage.setReplyMarkup(KeyboardButtonUtil.getUserMenu());
                            ComponentContainer.MY_BOT.sendMsg(sendMessage);
                        }
                    } else if (text.equals(KeyboardButtonConstants.BACK_TO_SUBJECT_MENU)) {
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
                                History history = new History(idHistory, chatId, collect.get(0).getSubject(), countQuestion, countTrueAnswer, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
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
                    } else if (text.equals(KeyboardButtonConstants.CHOOSE_SUBJECT)) {
                        if (Database.subjectsList.size() == 0) {
                            sendMessage.setText("Hozircha fanlar yo'q");
                            ComponentContainer.MY_BOT.sendMsg(sendMessage);
                        } else {
                            sendMessage.setText("Please Choice subject");
                            sendMessage.setReplyMarkup(KeyboardButtonUtil.getChoiceSubjectMenu());
                            ComponentContainer.MY_BOT.sendMsg(sendMessage);
                        }
                    } else if (text.equals(KeyboardButtonConstants.GET_HISTORY_PDF)) {
                        String customer1 = null;
                        for (History history : Database.historyList) {
                            if (history.getUserChatId().equals(chatId)){
                                customer1=history.getUserChatId();
                            }
                        }
                        if(customer1 == null){
                            sendMessage.setText("test ishlanmagan");
                            ComponentContainer.MY_BOT.sendMsg(sendMessage);
                        }else {
                            SendDocument sendDocument = new SendDocument();
                            sendDocument.setChatId(chatId);
                            sendDocument.setDocument(new InputFile(WorkWithFiles.generateCustomerHistoryPdfFile(chatId, Database.historyList)));
                            ComponentContainer.MY_BOT.sendMsg(sendDocument);
                        }
                    }else if (text.equals(KeyboardButtonConstants.CONTACT_WITH_ADMIN)) {

                        ComponentContainer.customerMap.put(chatId, true);
                        sendMessage.setText("Xabaringizni kiriting: ");
                        ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    }
                    else if (ComponentContainer.customerMap.containsKey(chatId)) {

                        ComponentContainer.customerMap.remove(chatId);

                        sendMessage.setText("Xabaringiz adminga jo'natildi.");
                        Message acceptMessage = ComponentContainer.MY_BOT.sendMsg(sendMessage);

                        String str = "ChatId : " + customer.getChatId() + "\nFull name: " + customer.getFirstName() +
                                "\nPhone number: " + customer.getPhoneNumber() +
                                "\nText: " + text;
                        ComponentContainer.ADMIN_CHAT_IDS.stream().map(adminChatId -> new SendMessage(adminChatId, str)).forEach(sendMessage1 -> {
                            sendMessage1.setReplyMarkup(InlineKeyboardUtil.getConnectMarkup(chatId, acceptMessage.getMessageId()));
                            Message message1 = ComponentContainer.MY_BOT.sendMsg(sendMessage1);
                            ComponentContainer.messagesMap.merge(acceptMessage, new ArrayList<>(List.of(message1)), (messages, messages2) -> {
                                messages.addAll(messages2); return messages;
                            });
                        });
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
                        }

                    }

                    //Manguberdi Part version 2.0 Elbek changed some parts don't worry everything is ok

                    else {
                        if (ComponentContainer.customerMap.containsKey(chatId)) {

                            ComponentContainer.customerMap.remove(chatId);

                            sendMessage.setText("Xabaringiz adminga jo'natildi.");
                            ComponentContainer.MY_BOT.sendMsg(sendMessage);

                            String str = "ChatId : " + customer.getChatId() + "\nFull name: " + customer.getFirstName() +
                                    "\nPhone number: " + customer.getPhoneNumber() +
                                    "\nText: " + text;
                            SendMessage sendMessage1 = new SendMessage(ComponentContainer.ADMIN_CHAT_IDS.get(0), str);
                            sendMessage1.setReplyMarkup(InlineKeyboardUtil.getConnectMarkup(chatId));
                            ComponentContainer.MY_BOT.sendMsg(sendMessage1);
                        }
                    }
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
                beginLocalTime = LocalTime.now();
                doTest = true;
                idHistory++;
                now=LocalDateTime.now();
            } else {
                sendMessage.setText("Buncha savol yoq");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                System.out.println(collect.size());
            }
        } else if (data.equals(InlineButtonConstants.ALL_CALLBACK)) {
            countQuestion = collect.size();
            Collections.shuffle(collect);
            for (int i = 0; i < countQuestion; i++) {
                Collections.shuffle(collect.get(i).getAnswer());
            }
            idHistory++;
            doTest = true;
            now=LocalDateTime.now();
        }
        if (doTest) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
            if (currentQuestion != 0) {
                StringBuilder sb = new StringBuilder();
//                sb.append("Wrong answer ❌");
                sb.append(collect.get(currentQuestion - 1).getTitle()).append("\n");
                sb.append("Correct answer -> ").append(collect.get(currentQuestion - 1).getCorrectAns()).append("\n");

                if (collect.get(currentQuestion - 1).getCorrectAns().
                        equals(collect.get(currentQuestion - 1).getAnswer().get(Integer.parseInt(data)))) {
                    countTrueAnswer++;
                    sb.append("Correct answer ✔️\n");
                    sb.append("Barakalla ").append(CustomerService.getCustomerByChatId(chatId).getFirstName()).append(" To'gri Javobni topdingiz \uD83E\uDD73").append("\n");
                    sb.append("Well done ✅").append(CustomerService.getCustomerByChatId(chatId).getFirstName()).append(" To'gri Javobni topdingiz \uD83E\uDD73").append("\n");
                } else {
                    sb.append("Wrong answer ❌\n");
                    sb.append("Correct answer -> ").append(collect.get(currentQuestion - 1).getCorrectAns()).append("\n");
                    sb.append("Afsuski javobingiz xato ").append(CustomerService.getCustomerByChatId(chatId).
                    sb.append("Wrong answer ❌").append(CustomerService.getCustomerByChatId(chatId).
                            getFirstName()).append(" \uD83D\uDE14").append("\n");
                }
                sendMessage.setText(String.valueOf(sb));
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            if (currentQuestion == countQuestion|| Duration.between(now,LocalDateTime.now()).getSeconds()>=20 ) {
                DeleteMessage deleteMessage1 = new DeleteMessage(chatId, message.getMessageId());
                ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
                doTest = false;
                sendMessage.setText("Test tugadi" + "\nNatijangiz->" + countTrueAnswer + "/" + countQuestion +"\nEnd time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                History history = new History(idHistory, chatId, collect.get(0).getSubject(), countQuestion, countTrueAnswer, LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss")));
                Database.historyList.add(history);
                WorkWithFiles.writeHistoryList();
                currentQuestion = 0;
                countTrueAnswer = 0;

            }

            else {
                if (currentQuestion == 0) {

                    sendMessage.setText("Test boshlandi " + now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    SendMessage sendMessage1 = new SendMessage();
                    sendMessage1.setChatId(chatId);
                    sendMessage1.setText("Time: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
                    Message message1 = ComponentContainer.MY_BOT.sendMsg(sendMessage1);
                    new MyThread(message1).start();
                }
                int size = collect.get(currentQuestion).getAnswer().size();
                sendMessage.setText(collect.get(currentQuestion).getTitle());
                sendMessage.setReplyMarkup(InlineKeyboardUtil.getAnswers(currentQuestion, size));
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                currentQuestion++;
            }


        }

    }
    //Teginilmasin>>

    static class MyThread extends Thread {

        Message editMessage;

        public MyThread(Message editMessage) {
            this.editMessage = editMessage;
        }


}
