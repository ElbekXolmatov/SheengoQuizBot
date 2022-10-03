package TelegramQuiz.entity;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Question {
    private int id;
    private String subject;
    private String title;
    private List<String> answer;
    private String correctAns;


}
