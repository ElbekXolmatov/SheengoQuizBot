package TelegramQuiz.container;

import TelegramQuiz.Bot.QuizBot;
import TelegramQuiz.enums.QuestionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentContainer {
    public static QuizBot MY_BOT = null;
    public static String BOT_USERNAME = "http://t.me/QuizSolver_bot";
    public static String BOT_TOKEN = "5608695595:AAFPUI0BYzzSoZD10DZGh1WdmrIPb3Pzuwg";
    public static List<String> ADMIN_CHAT_IDS = new ArrayList<>(List.of("1053744830", "781142775"));

    public static boolean startElection = false;

    // adminChatId, QuestionStatus
    public static Map<String, QuestionStatus> statusMap = new HashMap<>();
}
