package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent(value = "Application commands")
@RequiredArgsConstructor
public class ApplicationCommands {

    @ShellMethod(value = "test command", key = "test")
    public String hello(){
        return "Hello World!";
    }
}
