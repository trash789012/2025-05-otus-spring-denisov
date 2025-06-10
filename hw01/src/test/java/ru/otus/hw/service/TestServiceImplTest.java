package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    private static final String NO_QUESTIONS_FOUND = "Questions is null";

    private static final String QUESTIONS_IS_EMPTY = "Questions is empty";

    private static final String QUESTION_FORMAT = "%2d. %s";

    private static final String ANSWER_FORMAT = "      %s) %s";

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    TestServiceImpl testService;

    @Test
    @DisplayName("Должен вернуть ошибку, если DAO вернул Null")
    void shouldReturnExceptionWhenDaoReturnNull() {
        given(questionDao.findAll()).willReturn(null);

        var exception = assertThrows(RuntimeException.class, () -> {
           testService.executeTest();
        });

        assertTrue(exception.getMessage().contains(NO_QUESTIONS_FOUND));
    }

    @Test
    @DisplayName("Должен вернуть ошибку, если DAO не нашел вопросы")
    void shouldReturnExceptionWhenDaoReturnEmpty() {
        given(questionDao.findAll()).willReturn(Collections.emptyList());
        var exception = assertThrows(RuntimeException.class, () -> {
            testService.executeTest();
        });

        assertTrue(exception.getMessage().contains(QUESTIONS_IS_EMPTY));
    }

    @Test
    @DisplayName("Проверка вопросов и ответов")
    void shouldExecuteTest() {
        List<Question> questions = List.of(
                new Question("Which keyword is used to define a constant variable in Java ?",
                        List.of(
                                new Answer("var", false),
                                new Answer("final", true)
                        ))
        );

        given(questionDao.findAll()).willReturn(questions);

        testService.executeTest();

        verify(questionDao, times(1)).findAll();

        verify(ioService, times(1)).printFormattedLine("Please answer the questions below%n");
        verify(ioService, times(1)).printFormattedLine(QUESTION_FORMAT,
                1, "Which keyword is used to define a constant variable in Java ?");
        verify(ioService, times(1)).printFormattedLine(ANSWER_FORMAT, "1.1", "var");
    }
}