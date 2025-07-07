package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent(value = "Application commands")
@RequiredArgsConstructor
public class ApplicationCommands {

    private final CommandHandler commandHandler;

    @ShellMethod(value = "Start testing", key = {"start", "s"})
    public void runTesting(){
        commandHandler.runTesting();
    }

    @ShellMethod(value = "LogIn", key = {"login", "l"})
    public String logIn(){
        return commandHandler.logIn();
    }

    @ShellMethod(value = "LogOut", key = {"logout", "lo"})
    public String logOut(){
        return commandHandler.logOut();
    }

}
