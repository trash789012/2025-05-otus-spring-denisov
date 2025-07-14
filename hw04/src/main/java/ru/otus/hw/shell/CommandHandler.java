package ru.otus.hw.shell;

import org.springframework.shell.Availability;

public interface CommandHandler {
    void runTesting();

    String logIn();

    String logOut();

    Availability isStartTestingAvailable();

    Availability isLogInAvailable();

    Availability isLogOutAvailable();
}
