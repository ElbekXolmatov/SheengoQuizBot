package TelegramQuiz.entity;

import com.sun.jdi.request.StepRequest;
import lombok.*;
import org.glassfish.grizzly.streams.StreamInput;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString

public class History {
    private int id;
    private String userChatId;
    private String subject;
    private int countOfQuestions;
    private int correctAnswers;
    private String time;
}
