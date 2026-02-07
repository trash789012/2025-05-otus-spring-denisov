package ru.otus.hw.controllers.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GroupPageController {

    @GetMapping({"/profile/group/{id}", "/admin/group/{id}", "/admin/group/new"})
    public String editPage(@PathVariable(required = false) Long id) {
        return "group";
    }

}
