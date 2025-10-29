package ru.otus.hw.commands;

import org.h2.tools.Console;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.sql.SQLException;

@ShellComponent
public class ConsoleCommand {

    @ShellMethod(key = "h2c", value = "Запустить консоль")
    public String startConsole() {

        try {
            Console.main();
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }

        return "Console started";
    }

}
