package TelegramQuiz.Bot;


import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.db.Database;
import TelegramQuiz.files.WorkWithFiles;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Main {
    public static void main(String[] args) {
        try {
            WorkWithFiles.readCustomerList();
            WorkWithFiles.readHistoryList();
            WorkWithFiles.readQuestionsList();
            WorkWithFiles.readSubjectsList();
            WorkWithFiles.readAdminList();
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);

            QuizBot myBot = new QuizBot();
            ComponentContainer.MY_BOT = myBot;

            botsApi.registerBot(myBot);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}
