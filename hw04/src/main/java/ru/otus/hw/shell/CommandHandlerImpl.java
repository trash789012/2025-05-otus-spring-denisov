package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.stereotype.Component;
import ru.otus.hw.service.LocalizedIOService;
import ru.otus.hw.service.StudentService;
import ru.otus.hw.service.TestRunnerService;

@Component
@RequiredArgsConstructor
public class CommandHandlerImpl implements CommandHandler {

    private final TestRunnerService testRunnerService;

    private final StudentService studentService;

    private final LocalizedIOService localizedIOService;

    @Override
    public void runTesting() {
        testRunnerService.run();
    }

    @Override
    public String logIn() {
        studentService.logIn();
        return localizedIOService.getMessage("CommandHandler.log.in.success");
    }

    @Override
    public String logOut() {
        studentService.logOut();
        return localizedIOService.getMessage("CommandHandler.log.out.success");
    }

    @Override
    public Availability isStartTestingAvailable() {
        return studentService.getStudent() != null
                ? Availability.available()
                : Availability.unavailable(localizedIOService.getMessage("CommandHandler.login.start.failed"));
    }

    @Override
    public Availability isLogInAvailable() {
        return studentService.getStudent() == null
                ? Availability.available()
                : Availability.unavailable(localizedIOService.getMessage("CommandHandler.login.failed"));
    }

    @Override
    public Availability isLogOutAvailable() {
        return studentService.getStudent() != null
                ? Availability.available()
                : Availability.unavailable(localizedIOService.getMessage("CommandHandler.logout.failed"));
    }
}
