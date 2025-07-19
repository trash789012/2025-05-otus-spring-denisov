package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.h2.tools.Console;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.sql.SQLException;

@ShellComponent
@RequiredArgsConstructor
public class ConsoleCommands {

    @ShellMethod(value = "Start H2 Console", key = {"h2c", "h2console"})
    public String startConsole() {
        try {
            Console.main();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return "Console started";
    }
}
