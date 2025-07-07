package ru.otus.hw.service.converter;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Question;

@Component
public class Question2StringConverter implements QuestionConverter {
    private static final String QUESTION_FORMAT = "%2d. %s";

    private static final String ANSWER_FORMAT = "      %s) %s";

    @Override
    public String convertToString(Question question, int questionNumber) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(String.format(QUESTION_FORMAT, questionNumber, question.text()));

        var answerNumber = 1;
        for (var answer : question.answers()) {
            stringBuilder
                    .append(System.lineSeparator())
                    .append(String.format(ANSWER_FORMAT, answerNumber, answer.text()));
            answerNumber++;
        }

        return stringBuilder.toString();
    }
}
