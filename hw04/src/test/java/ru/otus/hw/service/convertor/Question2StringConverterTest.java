package ru.otus.hw.service.convertor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.service.converter.Question2StringConverter;
import ru.otus.hw.service.converter.QuestionConverter;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(classes = {Question2StringConverter.class})
public class Question2StringConverterTest {
    @Autowired
    private QuestionConverter question2StringConverter;

    @Test
    @DisplayName("Должен корректно работать, если нет списка ответов")
    void shouldReturnCorrectQuestionWithEmptyAnswers(){

        final String QUESTION = "What is your name?";

        var question = new Question(QUESTION, List.of());
        String result = question2StringConverter.convertToString(question, 1);

        String expected = " 1. " + QUESTION;

        assertThat(result).isEqualTo(expected);
    }

    @Test
    @DisplayName("Должен корректно отображать список вопросов и ответов")
    void shouldReturnCorrectQuestionWithCorrectAnswers(){
        final String QUESTION = "Which keyword is used to prevent a method from being overridden in Java?";

        var question = new Question(QUESTION, List.of(
                new Answer("static", false),
                new Answer("final", true),
                new Answer("private", false),
                new Answer("protected", false)
        ));

        String result = question2StringConverter.convertToString(question, 1);

        String expected = " 1. " + QUESTION + System.lineSeparator() +
                "      1) static" + System.lineSeparator() +
                "      2) final" + System.lineSeparator() +
                "      3) private" + System.lineSeparator() +
                "      4) protected";

        assertThat(result).isEqualTo(expected);
    }
}
