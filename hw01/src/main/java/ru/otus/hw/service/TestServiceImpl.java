package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private static final String NO_QUESTIONS_FOUND = "Questions is null";

    private static final String QUESTION_FORMAT = "%2d. %s";

    private static final String ANSWER_FORMAT = "      %s) %s";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        var questions = questionDao.findAll();
        if (questions == null || questions.isEmpty()) {
            throw new RuntimeException(NO_QUESTIONS_FOUND);
        }

        showQuestionsWithAnswers(questions);
    }

    private void showQuestionsWithAnswers(List<Question> questions) {
        int questionNumber = 0;
        int answerNumber;
        for (var question : questions) {
            questionNumber++;
            ioService.printFormattedLine(QUESTION_FORMAT, questionNumber, question.text());

            answerNumber = 0;
            for (var answers : question.answers()) {
                answerNumber++;
                String letter = questionNumber + "." + answerNumber;
                ioService.printFormattedLine(ANSWER_FORMAT, letter, answers.text());
            }
            ioService.printLine("");
        }
    }
}
