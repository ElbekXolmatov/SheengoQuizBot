package TelegramQuiz.util;

import TelegramQuiz.controller.UserController;
import TelegramQuiz.db.Database;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InlineKeyboardUtil {
    private static List<InlineKeyboardButton> getRow(InlineKeyboardButton... buttons) {
        return new ArrayList<>(List.of(buttons));
    }

    //<Teginilmasin
    public static ReplyKeyboard getCountOfQuestions() {
        InlineKeyboardButton five = new InlineKeyboardButton(InlineButtonConstants.FIVE);
        five.setCallbackData(InlineButtonConstants.FIVE_CALLBACK);
        InlineKeyboardButton ten = new InlineKeyboardButton(InlineButtonConstants.TEN);
        ten.setCallbackData(InlineButtonConstants.TEN_CALLBACK);
        InlineKeyboardButton twenty = new InlineKeyboardButton(InlineButtonConstants.TWENTY);
        twenty.setCallbackData(InlineButtonConstants.TWENTY_CALLBACK);
        InlineKeyboardButton thirty = new InlineKeyboardButton(InlineButtonConstants.THIRTY);
        thirty.setCallbackData(InlineButtonConstants.THIRTY_CALLBACK);
        InlineKeyboardButton all = new InlineKeyboardButton(InlineButtonConstants.ALL_QUESTIONS);
        all.setCallbackData(InlineButtonConstants.ALL_CALLBACK);

        return new InlineKeyboardMarkup(List.of(List.of(five, ten, twenty, thirty), List.of(all)));
    }
    public static InlineKeyboardMarkup getCancel(){
        InlineKeyboardButton button = new InlineKeyboardButton(InlineButtonConstants.CANCEL);
        button.setCallbackData(InlineButtonConstants.CANCEL_CALL_BACK);
        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }
    public static ReplyKeyboard getAnswers(int currentQuestion, int size) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        List<List<InlineKeyboardButton>> listButtons = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            InlineKeyboardButton a = new InlineKeyboardButton(UserController.collect.
                    get(currentQuestion).getAnswer().get(i));
            a.setCallbackData(String.valueOf(i));
            buttons.add(a);
//            listButtons.add(Collections.singletonList(buttons.get(i)));
        }listButtons.add(buttons);

        return new InlineKeyboardMarkup(listButtons);
    }
    //Teginilmasin>

    public static InlineKeyboardMarkup getChoiceSubjectMenu(){

        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        InlineKeyboardButton button;

        for (int i = 0; i < Database.subjectsList.size(); i++) {
            button = new InlineKeyboardButton(Database.subjectsList.get(i).getTitle());
            button.setCallbackData(String.valueOf(Database.subjectsList.get(i).getId()));
            keyboard.add(getRow(button));
        }

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        return markup;
    }
    public static InlineKeyboardMarkup getConnectMarkup(String chatId, Integer messageId) {
        InlineKeyboardButton button = new InlineKeyboardButton(InlineButtonConstants.REPLY_DEMO);
        button.setCallbackData(InlineButtonConstants.REPLY_CALL_BACK + "/" + chatId+"/"+messageId);

        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }
    public static ReplyKeyboard getSaveTestButtons() {
        InlineKeyboardButton button = new InlineKeyboardButton(InlineButtonConstants.ADD);
        button.setCallbackData(InlineButtonConstants.ADD_CALL_BACK);
        InlineKeyboardButton button1 = new InlineKeyboardButton(InlineButtonConstants.CANCEL);
        button1.setCallbackData(InlineButtonConstants.CANCEL_CALL_BACK);
        return new InlineKeyboardMarkup(List.of(List.of(button, button1)));
    }
}
