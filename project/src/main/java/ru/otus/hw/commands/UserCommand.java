package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.domain.User;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class UserCommand {

    private final UserRepository userRepository;

    @Transactional(readOnly = true) //todo: убрать
    @ShellMethod(key = "list-users", value = "Вывести всех пользователей")
    public String listUsers() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            return "Пользователей нет";
        }

        StringBuilder sb = new StringBuilder();
        users.forEach(u -> sb.append(u.getId())
                .append(". ").append(u.getName())
                .append(" [roles=").append(u.getRoles())
                .append("]\n"));
        return sb.toString();
    }

    @ShellMethod(key = "create-user", value = "Создать пользователя")
    public String createUser(@ShellOption String name, @ShellOption String password) {
        if (userRepository.findByName(name).isPresent()) {
            return "Пользователь уже существует";
        }

        User user = User.builder()
                .name(name)
                .password(password)
                .build();

        userRepository.save(user);
        return "✅ Пользователь создан: " + name;
    }

}
