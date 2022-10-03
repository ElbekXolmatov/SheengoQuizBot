package TelegramQuiz.container;

import TelegramQuiz.Bot.QuizBot;
import TelegramQuiz.entity.MessageData;
import TelegramQuiz.enums.QuestionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentContainer {
    public static QuizBot MY_BOT = null;

    public static String BOT_USERNAME = "t.me/TESTINGSOLVINGBOT";
    public static String BOT_TOKEN = "5538913781:AAHrZZocIwIFEGV2tO4Q5Ix7RqArryC7AB8";
    public static List<String> ADMIN_CHAT_IDS = new ArrayList<>(List.of("5246644409","1053744830", "781142775", "816777376"));


    // adminChatId, QuestionStatus
    public static Map<String, QuestionStatus> statusMap = new HashMap<>();
    public static Map<String, Boolean> customerMap = new HashMap<>();
    public static Map<String, MessageData> adminAnswerMap = new HashMap<>();
}
