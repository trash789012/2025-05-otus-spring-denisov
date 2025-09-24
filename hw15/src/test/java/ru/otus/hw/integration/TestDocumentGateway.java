package ru.otus.hw.integration;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.models.InvoiceDocument;
import ru.otus.hw.models.SalesOrder;

import java.util.List;

@MessagingGateway(defaultRequestChannel = "salesOrdersInput", defaultReplyTimeout = "500")
public interface TestDocumentGateway {
    @Gateway(replyChannel = "invoiceOutput")
    InvoiceDocument process(List<SalesOrder> orders);
}
