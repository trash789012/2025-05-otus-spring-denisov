package ru.otus.hw.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.GenericSelector;
import org.springframework.integration.dsl.AggregatorSpec;
import org.springframework.integration.dsl.FilterEndpointSpec;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;
import org.springframework.util.CollectionUtils;
import ru.otus.hw.models.DeliveryDocument;
import ru.otus.hw.models.InvoiceDocument;
import ru.otus.hw.models.SalesOrder;
import ru.otus.hw.services.DeliveryDocumentService;
import ru.otus.hw.services.InvoiceDocumentService;

import java.util.function.Consumer;

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
    public IntegrationFlow errorHandlingFlow() {
        return IntegrationFlow.from("errorChannel")
                .handle(msg -> {
                    log.error("***************** error msg: {}", msg.getPayload());
                })
                .get();
    }

    @Bean
    public IntegrationFlow salesOrdersFlow(DeliveryDocumentService deliveryDocumentService,
                                           InvoiceDocumentService invoiceDocumentService) {
        return IntegrationFlow.from(salesOrdersInput())
                .split()
                .filter(salesOrderSelector(), salesOrderFilterConfig())
                .channel(filteredSalesOrders())
                .handle(deliveryDocumentService, "generateDeliveryDocument")
                .aggregate(this::deliveryDocumentAggregator)
                .channel(createInvoice())
                .handle(invoiceDocumentService, "createInvoice")
                .transform(this::invoiceTransformer)
                .channel(invoiceOutput())
                .get();
    }

    private GenericSelector<SalesOrder> salesOrderSelector() {
        return order -> !CollectionUtils.isEmpty(order.items());
    }

    private Consumer<FilterEndpointSpec> salesOrderFilterConfig() {
        return filter -> filter
                .discardChannel("nullChannel")
                .throwExceptionOnRejection(false);
    }

    private void deliveryDocumentAggregator(AggregatorSpec aggregator) {
        aggregator
                .outputProcessor(group -> group.getMessages().stream()
                        .map(m -> (DeliveryDocument) m.getPayload())
                        .toList())
                .releaseStrategy(group -> true)
                .expireGroupsUponCompletion(true)
                .groupTimeout(1000L);
    }

    private InvoiceDocument invoiceTransformer(InvoiceDocument invoice) {
        return new InvoiceDocument(
                invoice.invoiceId(),
                invoice.totalPrice(),
                "{invoiceId: %s, totalPrice: %s}".formatted(invoice.invoiceId(), invoice.totalPrice())
        );
    }
}
