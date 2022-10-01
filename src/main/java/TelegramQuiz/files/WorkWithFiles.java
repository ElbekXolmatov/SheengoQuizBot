package TelegramQuiz.files;

import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import TelegramQuiz.entity.History;
import TelegramQuiz.entity.Question;
import TelegramQuiz.entity.Subject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;

public interface WorkWithFiles {

    Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().create();

    String BASE_FOLDER = "src/main/resources/documents";
    File CUSTOMER_FILE = new File(BASE_FOLDER, "customers.json");
    File SUBJECTS_FILE = new File(BASE_FOLDER, "subjects.json");
    File QUESTIONS_FILE = new File(BASE_FOLDER, "questions.json");
    File HISTORY_FILE = new File(BASE_FOLDER, "history.json");
    /////xullas hamma fanga alohida json kere

    static void readCustomerList(){
        if(!CUSTOMER_FILE.exists()) return;

        try {
            List customers = GSON.fromJson(new BufferedReader(new FileReader(CUSTOMER_FILE)),
                    new TypeToken<List<Customer>>() {
                    }.getType());
            Database.customerList.clear();
            Database.customerList.addAll(customers);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void writeCustomerList(){
        try (PrintWriter writer = new PrintWriter(CUSTOMER_FILE)) {
            writer.write(GSON.toJson(Database.customerList));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void readQuestionsList(){
        if(!QUESTIONS_FILE.exists()) return;

        try {
            List questions = GSON.fromJson(new BufferedReader(new FileReader(QUESTIONS_FILE)),
                    new TypeToken<List<Question>>() {
                    }.getType());
            Database.questionsList.clear();
            Database.questionsList.addAll(questions);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void writeQuestionsList(){
        try (PrintWriter writer = new PrintWriter(QUESTIONS_FILE)) {
            writer.write(GSON.toJson(Database.questionsList));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void readSubjectsList(){
        if(!SUBJECTS_FILE.exists()) return;

        try {
            List subjects = GSON.fromJson(new BufferedReader(new FileReader(SUBJECTS_FILE)),
                    new TypeToken<List<Subject>>() {
                    }.getType());
            Database.subjectsList.clear();
            Database.subjectsList.addAll(subjects);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void writeSubjectsList(){
        try (PrintWriter writer = new PrintWriter(SUBJECTS_FILE)) {
            writer.write(GSON.toJson(Database.subjectsList));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void readHistoryList(){
        if(!HISTORY_FILE.exists()) return;

        try {
            List history = GSON.fromJson(new BufferedReader(new FileReader(HISTORY_FILE)),
                    new TypeToken<List<History>>() {
                    }.getType());
            Database.historyList.clear();
            Database.historyList.addAll(history);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void writeHistoryList(){
        try (PrintWriter writer = new PrintWriter(HISTORY_FILE)) {
            writer.write(GSON.toJson(Database.historyList));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


}
