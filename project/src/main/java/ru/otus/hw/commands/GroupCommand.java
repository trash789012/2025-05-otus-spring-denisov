package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import ru.otus.hw.domain.Group;
import ru.otus.hw.repositories.GroupRepository;

import java.util.List;

@ShellComponent
@RequiredArgsConstructor
public class GroupCommand {

    private final GroupRepository groupRepository;

    @ShellMethod(key = "list-groups", value = "Вывести все группы")
    public String listGroups() {
        List<Group> groups = groupRepository.findAll();
        if (groups.isEmpty()) {
            return "Групп нет";
        }

        StringBuilder sb = new StringBuilder("=== GROUPS ===\n");
        groups.forEach(g -> sb.append(g.getId())
                .append(". ").append(g.getName())
                .append(" [members=").append(g.getMembers().size())
                .append("]\n"));
        return sb.toString();
    }

    @ShellMethod(key = "create-group", value = "Создать группу")
    public String createGroup(@ShellOption String name,
                              @ShellOption(defaultValue = "*") String description) {
        if (groupRepository.findByName(name).isPresent()) {
            return "Группа уже существует";
        }

        Group group = Group.builder()
                .name(name)
                .description(description)
                .build();

        groupRepository.save(group);
        return "✅ Группа создана: " + name;
    }

}
