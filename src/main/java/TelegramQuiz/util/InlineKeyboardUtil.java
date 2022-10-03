package TelegramQuiz.util;

import TelegramQuiz.controller.UserController;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InlineKeyboardUtil {
    public static InlineKeyboardMarkup getConfirmOrCancelMenu() {
        InlineKeyboardButton confirm = new InlineKeyboardButton(InlineButtonConstants.CONFIRM_DEMO);
        confirm.setCallbackData(InlineButtonConstants.CONFIRM_CALL_BACK);

        InlineKeyboardButton cancel = new InlineKeyboardButton(InlineButtonConstants.CANCEL_DEMO);
        cancel.setCallbackData(InlineButtonConstants.CANCEL_CALL_BACK);

        return new InlineKeyboardMarkup(List.of(
                List.of(confirm, cancel)
        ));
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


    public static InlineKeyboardMarkup getConnectMarkup(String chatId) {
        InlineKeyboardButton button = new InlineKeyboardButton(InlineButtonConstants.REPLY_DEMO);
        button.setCallbackData(InlineButtonConstants.REPLY_CALL_BACK + "/" + chatId);

        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }
}
