package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.Availability;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent(value = "Application commands")
@RequiredArgsConstructor
public class ApplicationCommands {

    private final CommandHandler commandHandler;

    @ShellMethod(value = "Start testing", key = {"start", "s"})
    @ShellMethodAvailability("isStartTestingAvailable")
    public void runTesting(){
        commandHandler.runTesting();
    }

    @ShellMethod(value = "LogIn", key = {"login", "l"})
    @ShellMethodAvailability("isLogInAvailable")
    public String logIn(){
        return commandHandler.logIn();
    }

    @ShellMethod(value = "LogOut", key = {"logout", "lo"})
    @ShellMethodAvailability("isLogOutAvailable")
    public String logOut(){
        return commandHandler.logOut();
    }

    public Availability isStartTestingAvailable() {
        return commandHandler.isStartTestingAvailable();
    }

    public Availability isLogInAvailable() {
        return commandHandler.isLogInAvailable();
    }

    public Availability isLogOutAvailable() {
        return commandHandler.isLogOutAvailable();
    }
}
