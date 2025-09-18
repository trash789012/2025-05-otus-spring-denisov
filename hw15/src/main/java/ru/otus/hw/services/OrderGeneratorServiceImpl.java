package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.InvoiceDocument;
import ru.otus.hw.models.SalesOrder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderGeneratorServiceImpl implements OrderGeneratorService {

    private final DocumentGateway documentGateway;

    private static final String[] SALES_ORDER_ITEMS = {
            "Установка компрессорная",
            "Компрессор ВП0-УХЛ",
            "БАК дозатор",
            "ВВГ 5х70",
            "Битум",
            "Тройник 168х11",
            "Перчатки Arctics",
    };

    private final Random random = new Random();

    @Override
    public void startGenerateOrders() {
        ForkJoinPool pool = ForkJoinPool.commonPool();

        for (int i = 0; i < 10; i++) {
            pool.execute(() -> {
                List<SalesOrder> orders = generateOrders();
                log.info("******* start create invoice document");
                InvoiceDocument invoiceDocument = documentGateway.process(orders);
                log.info("******* create invoice document: {}", invoiceDocument);
            });

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<SalesOrder> generateOrders() {

        int orderCount = random.nextInt(10);

        List<SalesOrder> orders = new ArrayList<>();

        for (int i = 0; i < orderCount; i++) {
            SalesOrder order = new SalesOrder(i + 1, generateSalesOrderItems());
            orders.add(order);
        }

        return orders;
    }

    private List<String> generateSalesOrderItems() {
        if (random.nextDouble() < 0.2) {
            return null;
        }

        int itemsCount = random.nextInt(5) + 2;

        List<String> items = new ArrayList<>();

        for (int i = 0; i < itemsCount; i++) {
            items.add(SALES_ORDER_ITEMS[random.nextInt(0, SALES_ORDER_ITEMS.length)]);
        }

        return items;
    }
}
