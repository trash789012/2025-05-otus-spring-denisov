package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import ru.otus.hw.models.DeliveryDocument;
import ru.otus.hw.models.InvoiceDocument;
import ru.otus.hw.models.SalesOrder;
import ru.otus.hw.services.DeliveryDocumentService;
import ru.otus.hw.services.InvoiceDocumentService;

@Slf4j
@Configuration
public class IntegrationConfig {

    @Bean
    public MessageChannelSpec<?, ?> salesOrdersInput() {
        return MessageChannels.queue(100);
    }

    @Bean
    public MessageChannelSpec<?, ?> filteredSalesOrders() {
        return MessageChannels.direct();
    }

    @Bean
    public MessageChannelSpec<?, ?> createInvoice() {
        return MessageChannels.direct();
    }

    @Bean
    public MessageChannelSpec<?, ?> invoiceOutput() {
        return MessageChannels.publishSubscribe();
    }

    @Bean
    public MessageChannelSpec<?, ?> errorChannel() {
        return MessageChannels.direct();
    }

    @Bean
    public PollerSpec poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(10);
    }

    @Bean
    public IntegrationFlow salesOrdersFlow(DeliveryDocumentService deliveryDocumentService,
                                           InvoiceDocumentService invoiceDocumentService) {
        return IntegrationFlow.from(salesOrdersInput())
                .split()
                .<SalesOrder>filter(order -> order.items() != null && !order.items().isEmpty(),
                        filter -> filter
                                .discardChannel("nullChannel")
                                .throwExceptionOnRejection(false))
                .channel(filteredSalesOrders())
                .handle(deliveryDocumentService, "generateDeliveryDocument")
                .aggregate(aggregator -> aggregator
                        .outputProcessor(group -> group.getMessages().stream()
                                .map(m -> (DeliveryDocument) m.getPayload())
                                .toList())
                        .releaseStrategy(group -> true)
                        .expireGroupsUponCompletion(true)
                        .groupTimeout(1000L)
                )
                .channel(createInvoice())
                .handle(invoiceDocumentService, "createInvoice")
                .<InvoiceDocument, InvoiceDocument>transform(invoice ->
                        new InvoiceDocument(invoice.invoiceId(), invoice.totalPrice(),
                                "{invoiceId: %s, totalPrice: %s}".formatted(invoice.invoiceId(), invoice.totalPrice())))
                .channel(invoiceOutput())
                .get();
    }

    @Bean
    public IntegrationFlow errorHandlingFlow() {
        return IntegrationFlow.from("errorChannel")
                .handle(msg -> {
                    log.error("***************** error msg: {}", msg.getPayload());
                })
                .get();
    }
}
