package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {
    private static final String NO_QUESTIONS_FOUND = "No questions found";

    private static final String QUESTION_FORMAT = "%2d. %s";

    private static final String ANSWER_FORMAT = "      %s) %s";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");

        var questions = questionDao.findAll();
        if (questions.isEmpty() || questions == null) {
            throw new RuntimeException(NO_QUESTIONS_FOUND);
        }

        int questionNumber = 0;
        int answerNumber;
        for (var question : questions) {
            questionNumber++;
            ioService.printFormattedLine(QUESTION_FORMAT, questionNumber, question.text());

            answerNumber = 0;
            for (var answers : question.answers()) {
                answerNumber++;
                var letter = questionNumber + "." + answerNumber;
                ioService.printFormattedLine(ANSWER_FORMAT, letter, answers.text());
            }
            ioService.printLine("");
        }

    }
}
