package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.DeliveryDocument;
import ru.otus.hw.models.OrderItemDocument;
import ru.otus.hw.models.SalesOrder;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DeliveryDocumentServiceImpl implements DeliveryDocumentService {

    private final Random random = new Random();

    @Override
    public DeliveryDocument generateDeliveryDocument(SalesOrder order) {
        int deliveryId = random.nextInt(100);

        List<OrderItemDocument> items = order.items().stream()
                .map(item -> new OrderItemDocument(
                        deliveryId,
                        order.orderId(),
                        item,
                        Math.random() * 100)
                )
                .toList();

        return new DeliveryDocument(deliveryId, order.orderId(), items);
    }

}
