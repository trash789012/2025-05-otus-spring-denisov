package ru.otus.hw.exceptions;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handeNotFoundException(Exception ex) {
        ex.printStackTrace();
        return new ModelAndView("customError",
                "errorText", ex.getMessage());
    }


}
