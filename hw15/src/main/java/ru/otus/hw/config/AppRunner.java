package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.services.OrderGeneratorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppRunner implements CommandLineRunner {

    private final OrderGeneratorService orderGeneratorService;

    @Override
    public void run(String... args) throws Exception {
        orderGeneratorService.startGenerateOrders();
    }

}
