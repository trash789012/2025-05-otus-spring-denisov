package ru.otus.hw.controllers.page;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SlotPageController {

    @GetMapping
    public String slotPage() {
        return "index";
    }

}
