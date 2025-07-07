package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Student;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final LocalizedIOService ioService;

    private Student student;

    @Override
    public void logIn() {
        var firstName = ioService.readStringWithPromptLocalized("StudentService.input.first.name");
        var lastName = ioService.readStringWithPromptLocalized("StudentService.input.last.name");
        student = new Student(firstName, lastName);
    }

    @Override
    public void logOut() {
        student = null;
    }

    @Override
    public Student getStudent() {
        return student;
    }
}
