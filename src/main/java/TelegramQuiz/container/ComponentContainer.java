package TelegramQuiz.container;

import TelegramQuiz.Bot.QuizBot;
import TelegramQuiz.enums.QuestionStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentContainer {
    public static QuizBot MY_BOT = null;
    public static String BOT_USERNAME = "SolveTestBot";
    public static String BOT_TOKEN = "5607797780:AAHROLWrt7ruCry5731APrzGe8yNJ4Joze8";
    public static List<String> ADMIN_CHAT_IDS = new ArrayList<>(List.of("1053744830"));

    public static boolean startElection = false;

    // adminChatId, QuestionStatus
    public static Map<String, QuestionStatus> statusMap = new HashMap<>();
}
