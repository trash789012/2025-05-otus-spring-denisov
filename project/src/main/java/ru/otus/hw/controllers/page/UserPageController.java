package ru.otus.hw.controllers.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class UserPageController {
    @GetMapping("/profile")
    public String userPageSelf() {
        return "profile";
    }

    @GetMapping({"/user/{id}", "/admin/user/{id}"})
    public String userPage(@PathVariable(required = false) Long id) {
        return "user";
    }
}