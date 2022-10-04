package TelegramQuiz.controller;

import TelegramQuiz.Enum.EnumSubject;
import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import TelegramQuiz.entity.MessageData;
import TelegramQuiz.entity.Subject;
import TelegramQuiz.enums.QuestionStatus;
import TelegramQuiz.files.WorkWithFiles;
import TelegramQuiz.qrcode.GenerateQRCode;
import TelegramQuiz.service.CustomerService;
import TelegramQuiz.service.SubjectService;
import TelegramQuiz.entity.Question;
import TelegramQuiz.files.WorkWithFiles;
import TelegramQuiz.util.InlineButtonConstants;
import TelegramQuiz.util.InlineKeyboardUtil;
import TelegramQuiz.util.KeyboardButtonConstants;
import TelegramQuiz.util.KeyboardButtonUtil;
import lombok.AllArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class AdminController {

    static EnumSubject enumSubject;
    static Subject newName;
    static Subject deleteSubject;


    static boolean isDeleted;
    static boolean isAdded;

    public static void handleMessage(User user, Message message) {

        String s = String.valueOf(message.getChatId());
        if (message.hasText()) {
            String text = message.getText();
            handleText(user, message, text);
        } else if (message.hasContact()) {
            Contact contact = message.getContact();
            handleContact(user, message, contact);
        }


    }

    private static void handleContact(User user, Message message, Contact contact) {


        String chatId = String.valueOf(message.getChatId());
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        sendMessage.setText("Menu: ");
        sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
        ComponentContainer.MY_BOT.sendMsg(sendMessage);

    }


    private static void handleText(User user, Message message, String text) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);

        if (text.equals("/start")) {
            sendMessage.setText("Hello " + user.getFirstName());
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        } else if (text.equalsIgnoreCase(KeyboardButtonConstants.WORK_WITH_SUBJECTS)) {
            sendMessage.setText("Choose: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getSubjectMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

            DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
        } else if (enumSubject == EnumSubject.CREATE) {
            Subject subject = new Subject();
            Subject subject1 = SubjectService.checkSubject(text);

            DeleteMessage deleteMessage = new DeleteMessage();
            deleteMessage.setChatId(chatId);
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
            if (subject1 == null) {
                sendMessage.setText(text + " added successfully");
                subject.setTitle(text);

                SubjectService.addCallBackData(subject, text);

                WorkWithFiles.writeSubjectsList();
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
            } else {
                sendMessage.setText(text + " already added. Please enter other subject name");
            }


            ComponentContainer.MY_BOT.sendMsg(sendMessage);
            ComponentContainer.MY_BOT.sendMsg(deleteMessage);
        } else if (text.equals(KeyboardButtonConstants.WORK_WITH_TESTS)) {

            ComponentContainer.statusMap.put(chatId, QuestionStatus.ENTERED_TEST_MENU);

            sendMessage.setText("Menu tanlang:");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getTestCRUD());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (enumSubject == EnumSubject.UPDATE) {
            String title = newName.getTitle();
            sendMessage.setText("A new name  " + text + "  of " + title);

            SubjectService.changeSubject(title, text);
            enumSubject = null;
            WorkWithFiles.writeSubjectsList();
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
            sendMessage.setText("Hello " + user.getFirstName());
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.GET_USERS_LIST_EXCEL)) {
            if (Database.customerList.size() == 0) {
                sendMessage.setText("No registered customers");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(WorkWithFiles.generateCustomerExcelFile(Database.customerList)));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);
        } else if (text.equals(KeyboardButtonConstants.GET_ALL_HISTORY_PDF)) {
            if (Database.historyList.size() == 0) {
                sendMessage.setText("History is empty");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                return;
            }
            SendDocument sendDocument = new SendDocument();
            sendDocument.setChatId(chatId);
            sendDocument.setDocument(new InputFile(WorkWithFiles.generateAllHistoryPdfFile(Database.historyList)));
            ComponentContainer.MY_BOT.sendMsg(sendDocument);
            DeleteMessage deleteMessag2 = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessag2);
        } else if (text.equals(KeyboardButtonConstants.WORK_WITH_ADMIN)) {
            sendMessage.setText("Chose option below: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.BACK_TO_MENU)) {
            sendMessage.setText("Main menu: ");
            sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminMenu());
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.SHOW_ADMIN_LIST)) {
            for (String adminChatId : ComponentContainer.ADMIN_CHAT_IDS) {
                for (Customer customer : Database.customerList) {
                    if (customer.getChatId().equals(adminChatId)) {
                        sendMessage.setText("First name: " + customer.getFirstName() + "\n"
                                + "Phone number: " + customer.getPhoneNumber());
                        ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    }
                }
            }
        } else if (text.equals(KeyboardButtonConstants.DELETE_ADMIN)) {
            isDeleted = true;
            sendMessage.setText("Enter number " + "\nExample: +998907777777");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (text.equals(KeyboardButtonConstants.ADD_ADMIN)) {
            isAdded = true;
            sendMessage.setText("Enter number " + "\nExample: +998907777777");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);
        } else if (isDeleted) {
            String numSub = text.substring(1);
            Customer customer1 = Database.customerList.stream().filter(customer -> customer.getPhoneNumber().contains(numSub)).findFirst().orElse(null);
            if (!text.matches("(\\+)?998\\d{9}")) {
                sendMessage.setText("Wrong number! ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (customer1 == null) {
                sendMessage.setText("User not found!");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                isDeleted = false;
            } else {
                isDeleted = false;
                customer1.setAdmin(false);
                ComponentContainer.ADMIN_CHAT_IDS.remove(customer1.getChatId());
                WorkWithFiles.writeCustomerList();
                sendMessage.setText("Successfully deleted");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        } else if (isAdded) {
            Customer customer = Database.customerList.stream().filter(customer1 -> customer1.getPhoneNumber().contains(text)).findFirst().orElse(null);
            if (!text.matches("(\\+)?998\\d{9}")) {
                sendMessage.setText("Wrong number! ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (ComponentContainer.ADMIN_CHAT_IDS.contains(customer.getChatId())) {
                sendMessage.setText(customer.getPhoneNumber() + " is already on the list");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            } else if (customer == null) {
                sendMessage.setText("User not found: ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                isAdded = false;
            } else {
                isAdded = false;
                customer.setAdmin(true);
                ComponentContainer.ADMIN_CHAT_IDS.add(customer.getChatId());
                sendMessage.setText("Successfully added ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getAdminCRUD());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                WorkWithFiles.writeCustomerList();
                WorkWithFiles.readCustomerList();
            }

        } else if (ComponentContainer.adminAnswerMap.containsKey(chatId)) {
            MessageData messageData = ComponentContainer.adminAnswerMap.get(chatId);

            Integer customerMessageId = messageData.getMessageId();

            String customerChatId = messageData.getCustomerChatId();
            Integer messageId = messageData.getMessage().getMessageId();
            String messageText = messageData.getMessage().getText();

            sendMessage.setChatId(customerChatId);
            sendMessage.setText("Admin ning javobi: " + text);
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

            EditMessageText editMessageText = new EditMessageText();
            editMessageText.setChatId(chatId);

            editMessageText.setText(messageText + "\n\n xabariga javob: \n\n " + text);
            editMessageText.setMessageId(messageId);
            ComponentContainer.MY_BOT.sendMsg(editMessageText);
            ComponentContainer.adminAnswerMap.remove(chatId);

            List<Message> mustMessageList = null;
            Message mustKey = null;

            for (Message keyMessage : ComponentContainer.messagesMap.keySet()) {
                if (keyMessage.getMessageId().equals(customerMessageId)) {
                    mustKey = keyMessage;
                    mustMessageList = ComponentContainer.messagesMap.get(keyMessage);
                    break;
                }
            }

            for (Message message1 : mustMessageList) {
                String adminChatId = message1.getChatId().toString();
                if (!adminChatId.equals(message.getChatId().toString())) {
                    EditMessageText editMessageText1 = new EditMessageText();
                    editMessageText1.setChatId(adminChatId);
                    editMessageText1.setText("Admin " + message.getFrom().getFirstName() + " answered for this question");
                    editMessageText1.setMessageId(message1.getMessageId());
                    ComponentContainer.MY_BOT.sendMsg(editMessageText1);
                }
            }

            ComponentContainer.messagesMap.remove(mustKey);

        }
        else {
            if (ComponentContainer.statusMap.containsKey(chatId)) {
                QuestionStatus adminStatus = ComponentContainer.statusMap.get(chatId);

                ArrayList<String> questions = null;
                if (ComponentContainer.questions.containsKey(chatId)) {
                    questions = ComponentContainer.questions.get(chatId);
                }

                if ((adminStatus.equals(QuestionStatus.ENTERED_TEST_MENU)
                        || adminStatus.equals(QuestionStatus.DELETE_TEST)
                        || adminStatus.equals(QuestionStatus.READ_TEST)
                        || adminStatus.equals(QuestionStatus.SUB_DELETE_TEST)
                        || adminStatus.equals(QuestionStatus.UPDATE_TEST)
                        || adminStatus.equals(QuestionStatus.SUB_UPDATE_TEST))
                        && text.equals(KeyboardButtonConstants.TEST_CREATE)) {

                    ComponentContainer.statusMap.put(chatId, QuestionStatus.CREATE_TEST);

                    sendMessage.setText("Qaysi fandan test qo'shmoqchisiz?\n\nMavjud fanlar ro'yxati:");
                    sendMessage.setReplyMarkup(InlineKeyboardUtil.getChoiceSubjectMenu());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                } else if ((adminStatus.equals(QuestionStatus.ENTERED_TEST_MENU)
                        || adminStatus.equals(QuestionStatus.CREATE_TEST)
                        || adminStatus.equals(QuestionStatus.DELETE_TEST)
                        || adminStatus.equals(QuestionStatus.SUB_DELETE_TEST)
                        || adminStatus.equals(QuestionStatus.UPDATE_TEST)
                        || adminStatus.equals(QuestionStatus.SUB_UPDATE_TEST))
                        && text.equals(KeyboardButtonConstants.TEST_READ)) {
                    ComponentContainer.statusMap.put(chatId, QuestionStatus.READ_TEST);

                    sendMessage.setText("Qaysi fandan testlarni ko'rmoqchisiz?\n\nMavjud fanlar ro'yxati:");
                    sendMessage.setReplyMarkup(InlineKeyboardUtil.getChoiceSubjectMenu());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                } else if ((adminStatus.equals(QuestionStatus.ENTERED_TEST_MENU)
                        || adminStatus.equals(QuestionStatus.CREATE_TEST)
                        || adminStatus.equals(QuestionStatus.READ_TEST)
                        || adminStatus.equals(QuestionStatus.SUB_DELETE_TEST)
                        || adminStatus.equals(QuestionStatus.UPDATE_TEST)
                        || adminStatus.equals(QuestionStatus.SUB_UPDATE_TEST)) &&
                        text.equals(KeyboardButtonConstants.TEST_DELETE)) {
                    ComponentContainer.statusMap.put(chatId, QuestionStatus.DELETE_TEST);

                    sendMessage.setText("Qaysi fandan testlarni o'chirmoqchisiz?\n\nMavjud fanlar ro'yxati:");
                    sendMessage.setReplyMarkup(InlineKeyboardUtil.getChoiceSubjectMenu());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                } else if ((adminStatus.equals(QuestionStatus.ENTERED_TEST_MENU)
                        || adminStatus.equals(QuestionStatus.CREATE_TEST)
                        || adminStatus.equals(QuestionStatus.READ_TEST)
                        || adminStatus.equals(QuestionStatus.SUB_DELETE_TEST)
                        || adminStatus.equals(QuestionStatus.SUB_UPDATE_TEST)) &&
                        text.equals(KeyboardButtonConstants.TEST_UPDATE)) {
                    ComponentContainer.statusMap.put(chatId, QuestionStatus.UPDATE_TEST);

                    sendMessage.setText("Qaysi fandan testlarni taxrirlamoqchisiz?\n\nMavjud fanlar ro'yxati:");
                    sendMessage.setReplyMarkup(InlineKeyboardUtil.getChoiceSubjectMenu());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                } else if (adminStatus.equals(QuestionStatus.CREATE_TEST)) {
                    if (ComponentContainer.questions.containsKey(chatId)) {
                        if (text.contains("?")) {
                            questions.add(text);
                            ComponentContainer.questions.put(chatId, questions);
                            sendMessage.setText("Variantlarni kiriting:");
                            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCancel());
                        } else if (text.contains("/") && text.contains("*")) {
                            questions.set(questions.size() - 1, questions.get(questions.size() - 1) + "/" + text);
                            ComponentContainer.questions.put(chatId, questions);
                            sendMessage.setText(ComponentContainer.questions.get(chatId).size() + 1 + ". Savolni kiriting:");
                            sendMessage.setReplyMarkup(InlineKeyboardUtil.getCancel());
                        } else {
                            sendMessage.setText("Kiritishda xatolik! Qayta kiriting♻️");
                        }
                        ComponentContainer.MY_BOT.sendMsg(sendMessage);
                    }
                } else if (adminStatus.equals(QuestionStatus.SUB_DELETE_TEST)) {
                    if (text.matches("[0-9]") && !text.startsWith("0")) {
                        String subject = Database.subjectsList.get(Integer.parseInt(ComponentContainer.CHOICEN_SUBJECT)).getTitle();
                        List<Question> choicenQuestions = Database.questionsList.stream().filter(question -> question.getSubject().equals(subject)).toList();
                        if (choicenQuestions.size() >= Integer.parseInt(text)) {
                            if (Database.questionsList.removeIf(question -> question.getTitle().equals(choicenQuestions.get(Integer.parseInt(text) - 1).getTitle()))) {
                                sendMessage.setText("Tanlangan test o'chirildi!");
                                ComponentContainer.statusMap.put(chatId, QuestionStatus.ENTERED_TEST_MENU);
                                WorkWithFiles.writeQuestionsList();
                                ComponentContainer.CHOICEN_SUBJECT = "";
                            }
                        } else {
                            sendMessage.setText("Kiritishda xatolik.\nQayta kiriting♻️");
                        }
                    } else {
                        sendMessage.setText("Kiritishda xatolik.\nQayta kiriting♻️");
                    }
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getTestCRUD());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                } else if (adminStatus.equals(QuestionStatus.SUB_UPDATE_TEST)) {
                    if (text.matches("[0-9]") && !text.startsWith("0")) {
                        List<Question> choicenQuestions = ComponentContainer.questionsForUpdate.get(chatId);
                        if (choicenQuestions.size() >= Integer.parseInt(text)) {
                            sendMessage.setText("⚠️Eslatma\nKiritilayotgan savol tarkibida ?(so'roq) belgisi bo'lishi shart!\n" + text + " - savolni yangi variantini kiriting:");
                            for (int i = 0; i < Database.questionsList.size(); i++) {
                                if (Database.questionsList.get(i).getTitle().equals(choicenQuestions.get(Integer.parseInt(text) - 1).getTitle())) {
                                    ComponentContainer.CHOICEN_QUESTION = String.valueOf(i);
                                    break;
                                }
                            }
                        } else {
                            sendMessage.setText("Kiritishda xatolik.\nQayta kiriting♻️");
                        }
                    } else if (text.contains("?")) {
                        Database.questionsList.get(Integer.parseInt(ComponentContainer.CHOICEN_QUESTION)).setTitle(text);
                        sendMessage.setText("Tanlangan test taxrirlandi!");
                        ComponentContainer.statusMap.put(chatId, QuestionStatus.ENTERED_TEST_MENU);
                        ComponentContainer.CHOICEN_SUBJECT = "";
                        WorkWithFiles.writeQuestionsList();
                        WorkWithFiles.readQuestionsList();
                    } else {
                        sendMessage.setText("Kiritishda xatolik.\nQayta kiriting♻️");
                    }
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getTestCRUD());
                    ComponentContainer.MY_BOT.sendMsg(sendMessage);
                }
            }
        }
    }


    public static void handleCallback(User user, Message message, String data) {
        String chatId = String.valueOf(message.getChatId());

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (ComponentContainer.statusMap.containsKey(chatId)) {
            QuestionStatus adminStatus = ComponentContainer.statusMap.get(chatId);

            if (data.matches("[0-9]") && adminStatus.equals(QuestionStatus.CREATE_TEST)) {
                ComponentContainer.CHOICEN_SUBJECT = data;
                sendMessage.setText("⚠️Savol kiritayotganda har bir savol oxirida \"?\"(so'roq) belgisi bo'lishi shart.\n➖Masalan: Yer yuzida nechta okean bor?" +
                        "\n\nVariantlar esa bir qatorda \"/\" belgisi bilan ajratilib to'g'ri javob oldiga \"*\" belgisi qo'yilishi shart.\n➖Masalan: 3 ta/*4 ta/5ta");
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                ComponentContainer.questions.put(chatId, new ArrayList<>());
                sendMessage.setText(ComponentContainer.questions.get(chatId).size() + 1 + ". Savolni kiriting:");
                sendMessage.setReplyMarkup(InlineKeyboardUtil.getCancel());
            } else if (data.matches("[0-9]") && adminStatus.equals(QuestionStatus.READ_TEST)) {
                ComponentContainer.statusMap.put(chatId, QuestionStatus.ENTERED_TEST_MENU);
                ComponentContainer.CHOICEN_SUBJECT = data;
                WorkWithFiles.readQuestionsList();
                List<Question> questionList = Database.questionsList;
                List<Subject> subjectList = Database.subjectsList;
                String subject = subjectList.get(Integer.parseInt(data)).getTitle();
                List<Question> choicenQuestions = questionList.stream().filter(question -> question.getSubject().equals(subject)).toList();
                String question = subject + " fanidan mavjud testlar\n\n";
                List<String> variants = new ArrayList<>();
                for (int i = 0; i < choicenQuestions.size(); i++) {
                    question += i + 1 + ") " + choicenQuestions.get(i).getTitle() + "\n";
                    variants = choicenQuestions.get(i).getAnswer();
                    for (int i1 = 0; i1 < variants.size(); i1++) {
                        question += i1 + 1 + ". " + variants.get(i1) + "\n";
                    }
                    question += "\n";
                }

                sendMessage.setText(question);
            } else if (data.matches("[0-9]") && adminStatus.equals(QuestionStatus.UPDATE_TEST)) {
                ComponentContainer.statusMap.put(chatId, QuestionStatus.SUB_UPDATE_TEST);
                ComponentContainer.CHOICEN_SUBJECT = data;

                List<Question> questionList = Database.questionsList;
                List<Subject> subjectList = Database.subjectsList;
                String subject = subjectList.get(Integer.parseInt(data)).getTitle();
                List<Question> choicenQuestions = questionList.stream().filter(question -> question.getSubject().equals(subject)).toList();
                String question = subject + " fanidan mavjud testlar\n\n";
                List<String> variants = new ArrayList<>();
                for (int i = 0; i < choicenQuestions.size(); i++) {
                    question += i + 1 + ") " + choicenQuestions.get(i).getTitle() + "\n";
                    variants = choicenQuestions.get(i).getAnswer();
                    for (int i1 = 0; i1 < variants.size(); i1++) {
                        question += i1 + 1 + ". " + variants.get(i1) + "\n";
                    }
                    question += "\n";
                }
                ComponentContainer.questionsForUpdate.put(chatId, choicenQuestions);
                sendMessage.setText(question);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                sendMessage.setText("Taxrirlamoqchi bo'lgan test raqamini kiriting :");
            } else if (data.matches("[0-9]") && adminStatus.equals(QuestionStatus.DELETE_TEST)) {
                ComponentContainer.statusMap.put(chatId, QuestionStatus.SUB_DELETE_TEST);
                ComponentContainer.CHOICEN_SUBJECT = data;

                List<Question> questionList = Database.questionsList;
                List<Subject> subjectList = Database.subjectsList;
                String subject = subjectList.get(Integer.parseInt(data)).getTitle();
                List<Question> choicenQuestions = questionList.stream().filter(question -> question.getSubject().equals(subject)).toList();
                String question = subject + " fanidan mavjud testlar\n\n";
                List<String> variants = new ArrayList<>();
                for (int i = 0; i < choicenQuestions.size(); i++) {
                    question += i + 1 + ") " + choicenQuestions.get(i).getTitle() + "\n";
                    variants = choicenQuestions.get(i).getAnswer();
                    for (int i1 = 0; i1 < variants.size(); i1++) {
                        question += i1 + 1 + ". " + variants.get(i1) + "\n";
                    }
                    question += "\n";
                }
                sendMessage.setText(question);
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                sendMessage.setText("O'chirmoqchi bo'lgan test raqamini kiriting :");
            } else if (data.equals(InlineButtonConstants.CANCEL_CALL_BACK)
                    && adminStatus.equals(QuestionStatus.CREATE_TEST)) {
                ComponentContainer.statusMap.put(chatId, QuestionStatus.SAVE_TESTS_PAGE);

                String questionList = "Yangi testlarni " + Database.subjectsList.get(Integer.parseInt(ComponentContainer.CHOICEN_SUBJECT)).getTitle() + " fani bazasiga qo'shamizmi?\n";
                ArrayList<String> questions = ComponentContainer.questions.get(chatId);
                String[] variants;
                for (int i = 0; i < questions.size(); i++) {
                    variants = questions.get(i).split("/");
                    if (variants.length > 1) {
                        questionList += i + 1 + ") " + variants[0] + "\n";
                        for (int i1 = 1; i1 < variants.length; i1++) {
                            questionList += i1 + ". " + variants[i1] + "\n";
                        }
                        questionList += "\n";
                    }
                }
                sendMessage.setText(questionList);
                sendMessage.setReplyMarkup(InlineKeyboardUtil.getSaveTestButtons());
            } else if (data.equals(InlineButtonConstants.CANCEL_CALL_BACK)
                    && adminStatus.equals(QuestionStatus.SAVE_TESTS_PAGE)) {
                ComponentContainer.statusMap.put(chatId, QuestionStatus.ENTERED_TEST_MENU);

                ComponentContainer.questions.remove(chatId);
                ComponentContainer.CHOICEN_SUBJECT = "";
                sendMessage.setText("Yangiliklar qo'shilmadi");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getTestCRUD());
            } else if (data.equals(InlineButtonConstants.ADD_CALL_BACK)
                    && adminStatus.equals(QuestionStatus.SAVE_TESTS_PAGE)) {
                ComponentContainer.statusMap.put(chatId, QuestionStatus.ENTERED_TEST_MENU);

                ArrayList<String> questionList = ComponentContainer.questions.get(chatId);
                String question = "";
                String correctAnswer = "";
                String subject = Database.subjectsList.get(Integer.parseInt(ComponentContainer.CHOICEN_SUBJECT)).getTitle();
                String[] variants;
                for (int i = 0; i < questionList.size(); i++) {
                    variants = questionList.get(i).split("/");
                    question = variants[0].trim();
                    for (int i1 = 1; i1 < variants.length; i1++) {
                        if (variants[i1].startsWith("*")) {
                            correctAnswer = variants[i1].substring(1);
                            variants[i1] = correctAnswer;
                        }
                    }
                    if (variants.length > 2) {
                        Database.questionsList.add(
                                new Question(Database.questionsList.size(),
                                        subject, question, List.of(Arrays.copyOfRange(variants, 1, variants.length)), correctAnswer));
                        sendMessage.setText("Yangi testlar qo'shildi!");
                    } else {
                        sendMessage.setText("Test qo'shishda xatolik!");
                    }
                }
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getTestCRUD());
                WorkWithFiles.writeQuestionsList();
                WorkWithFiles.readQuestionsList();
                ComponentContainer.CHOICEN_SUBJECT = "";
            }

        }


        ComponentContainer.MY_BOT.sendMsg(sendMessage);


        DeleteMessage deleteMessage = new DeleteMessage(chatId, message.getMessageId());
        ComponentContainer.MY_BOT.sendMsg(deleteMessage);

        if (data.startsWith(InlineButtonConstants.REPLY_CALL_BACK)) {
            String customerChatId = data.split("/")[1];
            Integer messageId = Integer.parseInt(data.split("/")[2]);

            ComponentContainer.adminAnswerMap.put(chatId, new MessageData(message, customerChatId, messageId));

            sendMessage.setText("Javobingizni kiriting: ");
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        }


        Subject subject = SubjectService.handeCallback(data);
        if (subject != null) {
            DeleteMessage deleteMessage9 = new DeleteMessage(chatId.toString(), message.getMessageId());

            ComponentContainer.MY_BOT.sendMsg(deleteMessage9);
            if (enumSubject == EnumSubject.DELETE) {
                enumSubject = null;
                sendMessage.setText("Really do you want to delete " + subject.getTitle());
                deleteSubject = subject;
                sendMessage.setReplyMarkup(KeyboardButtonUtil.deleteSubjectButton());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                return;
            }

            sendMessage.setText(" Enter new name for " + subject.getTitle());
            enumSubject = EnumSubject.UPDATE;
            newName = subject;
            ComponentContainer.MY_BOT.sendMsg(sendMessage);

        } else {
            DeleteMessage deleteMessage1 = new DeleteMessage(chatId, message.getMessageId());
            ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
            if (data.equalsIgnoreCase(KeyboardButtonConstants.CREATE_SUBJECT)) {

                ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
                deleteMessage1 = new DeleteMessage(chatId.toString(), message.getMessageId() - 2);


                ComponentContainer.MY_BOT.sendMsg(deleteMessage1);
                sendMessage.setText("Send title of new subject ");
                enumSubject = EnumSubject.CREATE;

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                DeleteMessage deleteMessage5 = new DeleteMessage(chatId.toString(), message.getMessageId());

                ComponentContainer.MY_BOT.sendMsg(deleteMessage5);

            } else if (data.equalsIgnoreCase(KeyboardButtonConstants.BACK)) {
                DeleteMessage deleteMessage4 = new DeleteMessage(chatId.toString(), message.getMessageId());

                ComponentContainer.MY_BOT.sendMsg(deleteMessage4);
                sendMessage.setText("Choose: ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getSubjectMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            } else if (data.equalsIgnoreCase(KeyboardButtonConstants.READ_SUBJECT)) {
                DeleteMessage deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId());

                ComponentContainer.MY_BOT.sendMsg(deleteMessage3);
                if (Database.subjectsList.size() == 0) {
                    sendMessage.setText("You do not have any subject for reading");
                } else {
                    sendMessage.setText(allSubjects());

                }
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

                DeleteMessage deleteMessage5 = new DeleteMessage(chatId.toString(), message.getMessageId());

                ComponentContainer.MY_BOT.sendMsg(deleteMessage5);

            } else if (data.equalsIgnoreCase(KeyboardButtonConstants.UPDATE_SUBJECT)) {
                DeleteMessage deleteMessage6 = new DeleteMessage(chatId.toString(), message.getMessageId());
                deleteMessage6 = new DeleteMessage(chatId.toString(), message.getMessageId() - 1);
                deleteMessage6 = new DeleteMessage(chatId.toString(), message.getMessageId() - 2);

                ComponentContainer.MY_BOT.sendMsg(deleteMessage6);

                if (Database.subjectsList.size() == 0) {
                    sendMessage.setText("You do not have any subject for updating");
                } else {
                    sendMessage.setText("which subject you want to update");


                }
                enumSubject = EnumSubject.UPDATE;
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getInlineButtonBySubject());

                ComponentContainer.MY_BOT.sendMsg(sendMessage);
                DeleteMessage deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId());
                deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId() - 1);
                deleteMessage3 = new DeleteMessage(chatId.toString(), message.getMessageId() - 2);

                ComponentContainer.MY_BOT.sendMsg(deleteMessage3);

            } else if (data.equalsIgnoreCase(KeyboardButtonConstants.DELETE_SUBJECT)) {
                DeleteMessage deleteMessage7 = new DeleteMessage(chatId.toString(), message.getMessageId());

                ComponentContainer.MY_BOT.sendMsg(deleteMessage7);
                enumSubject = EnumSubject.DELETE;
                if (Database.subjectsList.size() == 0) {
                    sendMessage.setText("You do not have any subject for deleting");
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
                } else {
                    sendMessage.setText("Which subjects do you want to delete ");
                    sendMessage.setReplyMarkup(KeyboardButtonUtil.getInlineButtonBySubject());
                }

                ComponentContainer.MY_BOT.sendMsg(sendMessage);


            } else if (data.equalsIgnoreCase(InlineButtonConstants.YES)) {
                DeleteMessage deleteMessage8 = new DeleteMessage(chatId.toString(), message.getMessageId());

                ComponentContainer.MY_BOT.sendMsg(deleteMessage8);
                Database.subjectsList.remove(deleteSubject);
                WorkWithFiles.writeSubjectsList();
                sendMessage.setText(deleteSubject.getTitle() + " deleted ");
                deleteSubject = null;
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getBackInlineButton());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);

            } else if (data.equalsIgnoreCase(InlineButtonConstants.NO)) {
                DeleteMessage deleteMessage9 = new DeleteMessage(chatId.toString(), message.getMessageId());

                ComponentContainer.MY_BOT.sendMsg(deleteMessage9);
                sendMessage.setText("Choose: ");
                sendMessage.setReplyMarkup(KeyboardButtonUtil.getSubjectMenu());
                ComponentContainer.MY_BOT.sendMsg(sendMessage);
            }
        }


    }

    public static String allSubjects() {
        StringBuilder allSubject = new StringBuilder("");
        String s = "";
        for (int i = 0; i < Database.subjectsList.size(); i++) {
            int index = i + 1;
            String title = Database.subjectsList.get(i).getTitle();
            s = s + index + " -> " + title;
            allSubject.append(s + "\n");
            s = "";
        }
        String AllSubject = String.valueOf(allSubject);
        return AllSubject;
    }
}

