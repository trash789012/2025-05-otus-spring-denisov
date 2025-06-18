package ru.otus.hw.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTest {

    private static final String NO_QUESTIONS_FOUND = "Questions is null";

    private static final String QUESTION_FORMAT = "%2d. %s";

    private static final String ANSWER_FORMAT = "      %s) %s";

    private static final String ERROR_READING = "Error Reading File";

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    @InjectMocks
    TestServiceImpl testService;

    @InjectMocks
    CsvQuestionDao csvQuestionDao;

    @Mock
    TestFileNameProvider fileNameProvider;

    @Test
    @DisplayName("Должен вернуть ошибку, если что-то не так с файлом при его чтении")
    void shouldReturnExceptionWhenFileRead() {
        given(fileNameProvider.getTestFileName()).willReturn(null);

        var exception = assertThrows(QuestionReadException.class, () -> {
            csvQuestionDao.findAll();
        });
        assert(exception.getMessage().contains(ERROR_READING));
    }

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
    @DisplayName("Проверка формата вопросов и ответов")
    void shouldExecuteTest() {

        final String QUESTION1 = "Which keyword is used to define a constant variable in Java ?";

        final String QUESTION2 = "Which method is used to start a thread executions in Java?";

        final String ANSWER_1_1 = "var";

        final String ANSWER_1_2 = "final";

        final String ANSWER_2_1 = "start()";

        final String ANSWER_2_2 = "execute()";

        List<Question> questions = List.of(
                new Question(QUESTION1,
                        List.of(
                                new Answer(ANSWER_1_1, false),
                                new Answer(ANSWER_1_2, true)
                        )),
                new Question(QUESTION2,
                        List.of(
                                new Answer(ANSWER_2_1, true),
                                new Answer(ANSWER_2_2, false)
                        ))
        );

        given(questionDao.findAll()).willReturn(questions);

        testService.executeTest();

        //проверяем, что findAll вызывался
        verify(questionDao, times(1)).findAll();

        //проверяем ответы от сервиса
        //вступительная фраза
        verify(ioService, times(1))
                .printFormattedLine("Please answer the questions below%n");

        //форматирование между вопросами
        verify(ioService, times(3)).printLine("");

        //первый вопрос (из мок данных)
        verify(ioService, times(1))
                .printFormattedLine(QUESTION_FORMAT, 1, QUESTION1);
        //первый вариант ответа
        verify(ioService, times(1))
                .printFormattedLine(ANSWER_FORMAT, "1.1", ANSWER_1_1);
        //второй вариант ответа
        verify(ioService, times(1))
                .printFormattedLine(ANSWER_FORMAT, "1.2", ANSWER_1_2);

        //второй вопрос (из мок данных)
        verify(ioService, times(1))
                .printFormattedLine(QUESTION_FORMAT, 2, QUESTION2);
        //первый вариант ответа (2 вопрос)
        verify(ioService, times(1))
                .printFormattedLine(ANSWER_FORMAT, "2.1", ANSWER_2_1);
        //второй вариант ответа (2 вопрос)
        verify(ioService, times(1))
                .printFormattedLine(ANSWER_FORMAT, "2.2", ANSWER_2_2);

    }
}