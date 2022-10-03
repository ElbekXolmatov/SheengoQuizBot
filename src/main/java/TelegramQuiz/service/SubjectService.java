package TelegramQuiz.service;

import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Subject;

public class SubjectService {
    public static Subject checkSubject(String text){
       return Database.subjectsList.stream().filter(subject -> subject.getTitle().equals(text))
                .findFirst().orElse(null);
    }

    public static void addCallBackData(Subject subject, String text) {
        subject.setCallBack(text);
        Database.subjectsList.add(subject);
    }

    public static Subject handeCallback(String data) {

        for (Subject subject : Database.subjectsList) {
            if(data.equalsIgnoreCase(subject.getCallBack())){
                return subject;
            }
        }
        return null;
    }

    public static void changeSubject(String s, String text) {
        for (Subject subject : Database.subjectsList) {
            if(subject.getTitle().equals(s)){
                subject.setTitle(text);
                subject.setCallBack(text);
                return;
            }
        }
    }

    public static String deleteSubject(String data) {
        for (Subject subject : Database.subjectsList) {
            if(data.equalsIgnoreCase(subject.getCallBack())){
                return data;
            }
        }
        return null;
    }
}
