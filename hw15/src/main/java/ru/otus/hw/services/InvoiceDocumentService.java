package ru.otus.hw.services;

import ru.otus.hw.models.DeliveryDocument;
import ru.otus.hw.models.InvoiceDocument;

import java.util.List;

public interface InvoiceDocumentService {
    InvoiceDocument createInvoice(List<DeliveryDocument> deliveryDocuments);
}
