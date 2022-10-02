package TelegramQuiz.db;


import TelegramQuiz.entity.Customer;
import TelegramQuiz.entity.History;
import TelegramQuiz.entity.Question;
import TelegramQuiz.entity.Subject;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

public interface Database {
    List<Customer> customerList = new ArrayList<>();
    List<Question>  questionsList = new ArrayList<>();
    List<Message> messageList = new ArrayList<>();
    List<Subject> subjectsList = new ArrayList<>();
    List<History> historyList = new ArrayList<>();

}
