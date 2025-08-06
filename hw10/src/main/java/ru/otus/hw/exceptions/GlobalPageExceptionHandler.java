package ru.otus.hw.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalPageExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ModelAndView handeNotFoundException(Exception ex) {
        return new ModelAndView("customError",
                "errorText", "Объект не найден");
    }
}
