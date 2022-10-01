package TelegramQuiz.files;

import TelegramQuiz.db.Database;
import TelegramQuiz.entity.Customer;
import TelegramQuiz.entity.History;
import TelegramQuiz.entity.Question;
import TelegramQuiz.entity.Subject;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.HorizontalAlignment;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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

    static File generateCustomerExcelFile(List<Customer> customerList) {
        File file = new File(BASE_FOLDER, "customerList.xlsx");

        try (FileOutputStream out = new FileOutputStream(file);
             XSSFWorkbook workbook = new XSSFWorkbook()
        ){
            XSSFSheet sheet = workbook.createSheet();

            XSSFRow header = sheet.createRow(0);
            header.createCell(0).setCellValue("Id");
            header.createCell(1).setCellValue("Chat Id");
            header.createCell(2).setCellValue("First Name");
            header.createCell(3).setCellValue("Last Name");
            header.createCell(4).setCellValue("Phone Number");

            for (int i = 0; i < customerList.size(); i++) {
                XSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);
                row.createCell(1).setCellValue(customerList.get(i).getChatId());
                row.createCell(2).setCellValue(customerList.get(i).getFirstName());
                row.createCell(3).setCellValue((customerList.get(i).getLastName()==null? "-": customerList.get(i).getLastName()));
                row.createCell(4).setCellValue(customerList.get(i).getPhoneNumber());
            }

            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
        }catch (IOException e){
            e.printStackTrace();
        }
        return file;
    }

    static File generateAllHistoryPdfFile(List<History> historyList) {
        File file = new File (BASE_FOLDER, "history.pdf");

        try (PdfWriter pdfWriter = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(pdfWriter);
            Document document = new Document(pdfDocument)){

            pdfDocument.addNewPage();
            Paragraph paragraph = new Paragraph();
            document.add(paragraph);

            float[] columns = {100f,100f,100f,100f,100f,100f};
            Table table = new Table(columns);
            table.setHorizontalAlignment(HorizontalAlignment.CENTER);

            String[] header = {"Id", "User chat Id", "Subject", "Count of questions", "Correct Answers", "Time"};

            for (String s : header) {
                table.addCell(s);
            }
            int i = 0;
            for (History history : historyList) {
                table.addCell(String.valueOf(i + 1));
                table.addCell(history.getUserChatId());
                table.addCell(history.getSubject());
                table.addCell(String.valueOf(history.getCountOfQuestions()));
                table.addCell(String.valueOf(history.getCorrectAnswers()));
                table.addCell(history.getTime());
                i++;
            }
            document.add(table);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

}
