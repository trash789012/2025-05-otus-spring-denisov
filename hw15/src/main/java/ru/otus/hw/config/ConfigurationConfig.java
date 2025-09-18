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

import java.util.List;

@Slf4j
@Configuration
public class ConfigurationConfig {

    @Bean
    public MessageChannelSpec<?, ?> salesOrdersInput() {
        return MessageChannels.queue(100);
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
    public PollerSpec poller() {
        return Pollers.fixedRate(100).maxMessagesPerPoll(10);
    }

    @Bean
    public IntegrationFlow salesOrdersFlow(DeliveryDocumentService deliveryDocumentService,
                                           InvoiceDocumentService invoiceDocumentService) {
        return IntegrationFlow.from(salesOrdersInput())
                //сплит по списку SalesOrders (заказы на поставку)
                .split()
                //только для заказов с непустыми позициями
                .<SalesOrder>filter(order -> order.items() != null && !order.items().isEmpty(),
                        filter -> filter
                                .discardChannel("nullChannel")
                                .throwExceptionOnRejection(false))
                //создание заказа на поставку из каждого отдельного заказа
                .handle(deliveryDocumentService, "generateDeliveryDocument")
                //агрегация на поставки, а не на заказы
                .aggregate(aggregator -> aggregator
                        .outputProcessor(group -> {
                            List<DeliveryDocument> deliveries = group.getMessages().stream()
                                    .map(m -> (DeliveryDocument) m.getPayload())
                                    .toList();
                            return deliveries;
                        })
                        .releaseStrategy(group -> group.size() >= 1)
                        .expireGroupsUponCompletion(true)
                )
                //создание инвойса на весь объем поставок
                .channel(createInvoice())
                .handle(invoiceDocumentService, "createInvoice")
                .<InvoiceDocument, InvoiceDocument>transform(invoice ->
                        new InvoiceDocument(invoice.invoiceId(),
                                invoice.totalPrice(),
                                "{invoiceId: %s, totalPrice: %s}".formatted(invoice.invoiceId(), invoice.totalPrice())))
                .channel(invoiceOutput())
                .get();
    }
}
