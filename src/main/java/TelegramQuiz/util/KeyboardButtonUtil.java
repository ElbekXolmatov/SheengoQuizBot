package TelegramQuiz.util;

import TelegramQuiz.container.ComponentContainer;
import TelegramQuiz.db.Database;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButtonPollType;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
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
//<Teginilmasin
    public static ReplyKeyboard getChoiceSubjectMenu() {

        List<KeyboardRow> rows=new ArrayList<>();
        for (int i = 0; i < Database.subjectsList.size(); i+=2) {
            if (Database.subjectsList.size()==(i+1)){
                KeyboardButton button = getButton(Database.subjectsList.get(i).getTitle());
                KeyboardButton backMenu = getButton(KeyboardButtonConstants.BACK_TO_MAIN_MENU);
                KeyboardRow row = getRow(button,backMenu);
                rows.add(row);
            }
            else {
            KeyboardButton button = getButton(Database.subjectsList.get(i).getTitle());
            KeyboardButton button1 = getButton(Database.subjectsList.get(i+1).getTitle());
            KeyboardRow row = getRow(button,button1);
            rows.add(row);
            }
        }
        if (Database.subjectsList.size()%2==0){
            KeyboardButton backMenu = getButton(KeyboardButtonConstants.BACK_TO_MAIN_MENU);
            KeyboardRow row = getRow(backMenu);
            rows.add(row);
        }
        return getMarkup(rows);
    }
    public static ReplyKeyboard backToSubjectsMenu() {

      return getMarkup(getRowList(getRow(getButton(KeyboardButtonConstants.BACK_TO_SUBJECT_MENU),
               getButton(KeyboardButtonConstants.BACK_TO_MAIN_MENU))));
    }
    //Teginilmasin>
    public static InlineKeyboardMarkup getSubjectMenu() {
        InlineKeyboardButton button1=new InlineKeyboardButton();
        button1.setText("CREATE SUBJECT");
        button1.setCallbackData("subject");

        InlineKeyboardButton button2=new InlineKeyboardButton();
        button2.setText("READ SUBJECTS");
        button2.setCallbackData("read");

        InlineKeyboardButton button3=new InlineKeyboardButton();
        button3.setText("UPDATE SUBJECT");
        button3.setCallbackData("Update");

        InlineKeyboardButton button4=new InlineKeyboardButton();
        button4.setText("DELETE SUBJECT");
        button4.setCallbackData("delete");

        List<InlineKeyboardButton> row1=new ArrayList<>();
        row1.add(button1);
        row1.add(button2);

        List<InlineKeyboardButton> row2=new ArrayList<>();
        row2.add(button3);
        row2.add(button4);

        List<List<InlineKeyboardButton>> rows=new ArrayList<>();
        rows.add(row1);
        rows.add(row2);

        InlineKeyboardMarkup markup=new InlineKeyboardMarkup();

        markup.setKeyboard(rows);

        return markup;



    }

    public static InlineKeyboardMarkup getBackInlineButton() {
        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText("BACK");
        button.setCallbackData("BACK");
        List<InlineKeyboardButton> row=new ArrayList<>();
        row.add(button);

        List<List<InlineKeyboardButton>> rows=new ArrayList<>();

        rows.add(row);

        InlineKeyboardMarkup markupBack=new InlineKeyboardMarkup();
        markupBack.setKeyboard(rows);

        return markupBack;
    }

    public static ReplyKeyboard getInlineButtonBySubject() {


        List<List<InlineKeyboardButton>> rows=new ArrayList<>();

        for (int i = 0; i <= Database.subjectsList.size(); i++) {
            InlineKeyboardButton button=new InlineKeyboardButton();

            if(i==Database.subjectsList.size()){
                button.setText("BACK");
                button.setCallbackData("BACK");
            }else {

                button.setText(Database.subjectsList.get(i).getTitle());
                button.setCallbackData(Database.subjectsList.get(i).getTitle());
            }
                List<InlineKeyboardButton> row = new ArrayList<>();

            row.add(button);
            rows.add(row);

        }

        InlineKeyboardButton button1=new InlineKeyboardButton();
        button1.setText("BACK");
        button1.setCallbackData("BACK");

        InlineKeyboardMarkup markupBack=new InlineKeyboardMarkup();
        markupBack.setKeyboard(rows);


        return markupBack;
    }

    public static InlineKeyboardMarkup deleteSubjectButton() {
        InlineKeyboardButton button=new InlineKeyboardButton();
        button.setText("YES");
        button.setCallbackData("YES");

        InlineKeyboardButton button1=new InlineKeyboardButton();
        button1.setText("NO");
        button1.setCallbackData("NO");

        List<InlineKeyboardButton> row1=new ArrayList<>();
        row1.add(button);
        row1.add(button1);

        List<List<InlineKeyboardButton>> rows=new ArrayList<>();

        rows.add(row1);

        InlineKeyboardMarkup markup=new InlineKeyboardMarkup();
        markup.setKeyboard(rows);

        return markup;

    }

    public static ReplyKeyboard getAdminCRUD() {

        List<KeyboardRow> rowList = getRowList(
                getRow(
                        getButton(KeyboardButtonConstants.ADD_ADMIN),
                        getButton(KeyboardButtonConstants.SHOW_ADMIN_LIST)
                ),
                getRow(
                        getButton(KeyboardButtonConstants.DELETE_ADMIN),
                        getButton(KeyboardButtonConstants.BACK_TO_MENU)
                ));

        return getMarkup(rowList);
    }

}
