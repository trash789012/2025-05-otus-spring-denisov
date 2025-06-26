package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;
import ru.otus.hw.service.converter.QuestionConverter;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final int MIN_NUMBER_OF_ANSWERS = 1;

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    private final QuestionConverter questionConverter;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException("Questions is null");
        }

        showQuestionsWithAnswers(questions, testResult);

        return testResult;
    }

    private void showQuestionsWithAnswers(List<Question> questions, TestResult testResult) {
        int questionNumber = 1;
        for (var question : questions) {
            var userAnswer = processReadingAnswers(question, questionNumber); //запрос ответа
            checkCorrectAndApplyAnswer(testResult, question, questionNumber, userAnswer); //проверка ответа

            questionNumber++;
        }
    }

    private static void checkCorrectAndApplyAnswer(TestResult testResult, Question question, int questionNumber,
                                                   int userAnswer) {
        var isAnswerValid = question.answers().get(userAnswer - MIN_NUMBER_OF_ANSWERS).isCorrect();

        testResult.applyAnswer(question, isAnswerValid);
    }

    private int processReadingAnswers(Question question, int questionNumber) {
        ioService.printLineLocalized(questionConverter.convertToString(question, questionNumber));

        var userAnswer = ioService.readIntForRangeLocalized(
                MIN_NUMBER_OF_ANSWERS,
                question.answers().size(),
                "Not Valid Answer Number"
        );

        ioService.printLine("");

        return userAnswer;
    }

}
