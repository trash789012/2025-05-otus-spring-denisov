package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.service.converter.QuestionConverter;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {TestServiceImpl.class})
public class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @MockitoBean
    QuestionConverter questionConverter;

    @Autowired
    TestServiceImpl testService;

    @Test
    @DisplayName("Должен вернуть корректные ответы на тестирование")
    void shouldReturnIsCorrectAnswers() {
        List<Question> questions = List.of(
                new Question("Which keyword is used to prevent a method from being overridden in Java?",
                        List.of(
                                new Answer("static", false),
                                new Answer("final", true),
                                new Answer("private", false),
                                new Answer("protected", false)
                        )),
                new Question("What does the super() keyword refer to in Java?",
                        List.of(
                                new Answer("The current class instance", false),
                                new Answer("A static method in the same class", false),
                                new Answer("A reserved keyword with no use", false),
                                new Answer("The immediate parent class constructor", true)
                        ))
        );

        //Настраиваем, что возвращает дао для вопросов
        given(questionDao.findAll()).willReturn(questions);
        //Чтобы ответы не были пустыми, задаем поведение, как если бы отвечал человек
        given(ioService.readIntForRangeLocalized(anyInt(), anyInt(), anyString()))
                .willReturn(2)
                .willReturn(4);

        //Сравнение
        var testResult = testService.executeTestFor(new Student("Denis", "Sokolov"));

        assertThat(testResult).isNotNull();
        assertThat(testResult.getRightAnswersCount()).isEqualTo(2);

        //проверка, что API вызывался нужное количество раз
        verify(questionDao, times(1)).findAll();
        verify(ioService, times(2)).readIntForRangeLocalized(anyInt(), anyInt(), anyString());
        verify(questionConverter, times(2)).convertToString(any(Question.class), anyInt());
    }

    @Test
    @DisplayName("Проверка формата вопросов и ответов")
    void shouldExecuteTest() {

        final String QUESTION_TEXT = "Some question text...";

        List<Question> questions = List.of(
                new Question("QUESTION1",
                        List.of(
                                new Answer("ANSWER1", false),
                                new Answer("ANSWER2", true)
                        )),
                new Question("QUESTION2",
                        List.of(
                                new Answer("ANSWER1", true),
                                new Answer("ANSWER2", false)
                        ))
        );

        given(questionDao.findAll()).willReturn(questions);

        given(questionConverter.convertToString(any(Question.class), anyInt())).willReturn(QUESTION_TEXT);

        //Чтобы ответы не были пустыми, задаем поведение, как если бы отвечал человек
        given(ioService.readIntForRangeLocalized(anyInt(), anyInt(), anyString()))
                .willReturn(2)
                .willReturn(1);

        testService.executeTestFor(new Student("Denis", "Ivanov"));

        //проверяем, что findAll вызывался
        verify(questionDao, times(1)).findAll();

        //проверяем ответы от сервиса
        //форматирование между вопросами
        verify(ioService, times(4)).printLine("");

        //вопрос целиком
        verify(ioService, times(2))
                .printLine(QUESTION_TEXT);
    }

}
