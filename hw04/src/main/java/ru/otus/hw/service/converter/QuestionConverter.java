package ru.otus.hw.service.converter;

import ru.otus.hw.domain.Question;

public interface QuestionConverter {
    String convertToString(Question question, int questionNumber);
}
