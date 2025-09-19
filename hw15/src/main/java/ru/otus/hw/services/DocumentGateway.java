package ru.otus.hw.services;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import ru.otus.hw.models.InvoiceDocument;
import ru.otus.hw.models.SalesOrder;

import java.util.List;

@MessagingGateway(errorChannel = "errorChannel")
public interface DocumentGateway {
    @Gateway(requestChannel = "salesOrdersInput", replyChannel = "invoiceOutput")
    InvoiceDocument process(List<SalesOrder> orders);
}
