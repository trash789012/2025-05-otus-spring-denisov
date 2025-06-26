package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Component
public class CsvQuestionDao implements QuestionDao {
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
                throw new QuestionReadException("Empty Question List");
            }

            return questionDtoList.stream().map(QuestionDto::toDomainObject).toList();
        } catch (IOException e) {
            throw new QuestionReadException("Error Reading File", e);
        } catch (RuntimeException e) {
            throw new QuestionReadException("Error Parsing File", e);
        }
    }

    private InputStream getInputStream() throws IOException {
        var path = fileNameProvider.getTestFileName();
        if (path == null) {
            throw new IOException("File Not Found");
        }

        if (!path.endsWith(".csv")) {
            throw new IOException("Extension not csv");
        }

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("File Not Loaded" + path);
        }

        return inputStream;
    }
}
