package ru.otus.hw.services;

import ru.otus.hw.models.DeliveryDocument;
import ru.otus.hw.models.SalesOrder;

public interface DeliveryDocumentService {
    DeliveryDocument generateDeliveryDocument(SalesOrder order);
}
