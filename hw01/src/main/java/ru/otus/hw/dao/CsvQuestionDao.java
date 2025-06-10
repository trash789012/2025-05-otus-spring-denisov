package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private static final String FILE_NOT_FOUND = "File Not Found";

    private static final String INCORRECT_EXTENSION = "Extension not csv";

    private static final String FILE_NOT_LOADED = "File Not Loaded";

    private static final String ERROR_READING = "Error Reading File";

    private static final String ERROR_PARSING = "Error Parsing File";

    private static final String EMPTY_QUESTION_LIST = "Empty Question List";

    private static final int SKIP_ROW_COUNT = 1;

    private static final char SEPARATOR = ';';

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        try (var inputStream = getInputStream();
             var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            CsvToBean<QuestionDto> csvToBean = new CsvToBeanBuilder<QuestionDto>(reader)
                    .withSeparator(SEPARATOR)
                    .withSkipLines(SKIP_ROW_COUNT)
                    .withType(QuestionDto.class)
                    .withOrderedResults(true)
                    .build();

            List<QuestionDto> questionDtoList = csvToBean.stream().toList();
            if (questionDtoList.isEmpty()) {
                throw new QuestionReadException(EMPTY_QUESTION_LIST);
            }

            return questionDtoList.stream().map(QuestionDto::toDomainObject).toList();
        } catch (IOException e) {
            throw new QuestionReadException(ERROR_READING, e);
        } catch (RuntimeException e) {
            throw new QuestionReadException(ERROR_PARSING, e);
        }
    }

    private InputStream getInputStream() throws IOException {
        var path = fileNameProvider.getTestFileName();
        if (path.isEmpty() || !path.endsWith(".csv") || path == null) {
            throw new IOException(FILE_NOT_FOUND);
        }

        if (!path.endsWith(".csv")) {
            throw new IOException(INCORRECT_EXTENSION);
        }

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException(FILE_NOT_LOADED + path);
        }

        return inputStream;
    }
}
