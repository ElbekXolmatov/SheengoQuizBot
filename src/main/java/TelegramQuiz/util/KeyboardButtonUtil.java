package TelegramQuiz.util;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class KeyboardButtonUtil {

    public static ReplyKeyboard getAdminMenu() {
        List<KeyboardRow> rowList = getRowList(
                getRow(
                        getButton(KeyboardButtonConstants.GET_USERS_LIST_EXCEL),
                        getButton(KeyboardButtonConstants.GET_ALL_HISTORY_PDF)),
                getRow(
                        getButton(KeyboardButtonConstants.WORK_WITH_SUBJECTS),
                        getButton(KeyboardButtonConstants.WORK_WITH_TESTS)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.WORK_WITH_ADMIN)
                ));

        return getMarkup(rowList);
    }
    public static ReplyKeyboard getUserMenu() {
        List<KeyboardRow> rowList = getRowList(
                getRow(
                        getButton(KeyboardButtonConstants.CHOOSE_SUBJECT)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.GET_HISTORY_PDF)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.CONTACT_WITH_ADMIN)
                ));

        return getMarkup(rowList);
    }

    private static KeyboardButton getButton(String demo) {
        return new KeyboardButton(demo);
    }

    private static KeyboardRow getRow(KeyboardButton... buttons) {
        return new KeyboardRow(List.of(buttons));
    }

    private static List<KeyboardRow> getRowList(KeyboardRow... rows) {
        return List.of(rows);
    }

    private static ReplyKeyboardMarkup getMarkup(List<KeyboardRow> rowList) {
        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup(rowList);
        markup.setResizeKeyboard(true);
        markup.setSelective(true);
        return markup;
    }

    public static ReplyKeyboard getContactMenu() {
        KeyboardButton button = getButton("Raqamingizni jo'nating.");
        button.setRequestContact(true);

        return getMarkup(getRowList(getRow(button)));
    }

}
