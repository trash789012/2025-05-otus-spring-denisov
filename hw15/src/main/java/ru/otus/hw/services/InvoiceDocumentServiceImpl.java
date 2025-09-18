package ru.otus.hw.services;

import org.springframework.stereotype.Service;
import ru.otus.hw.models.DeliveryDocument;
import ru.otus.hw.models.InvoiceDocument;
import ru.otus.hw.models.OrderItemDocument;

import java.util.List;
import java.util.Random;

@Service
public class InvoiceDocumentServiceImpl implements InvoiceDocumentService {

    private final Random random = new Random();

    @Override
    public InvoiceDocument createInvoice(List<DeliveryDocument> deliveryDocuments) {

        double sum = deliveryDocuments.stream()
                .flatMap(delivery -> delivery.items().stream())
                .mapToDouble(OrderItemDocument::price)
                .sum();

        return new InvoiceDocument(random.nextInt(), sum, null);
    }
}
