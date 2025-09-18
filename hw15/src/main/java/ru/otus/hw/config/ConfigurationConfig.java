package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.MessageChannelSpec;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.dsl.PollerSpec;
import org.springframework.integration.dsl.Pollers;

@Configuration
public class ConfigurationConfig {

    @Bean
    public MessageChannelSpec<?, ?> salesOrdersInput() {
        return MessageChannels.queue(100);
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
    public IntegrationFlow salesOrdersFlow() {
        return IntegrationFlow.from(salesOrdersInput())
                //только заказы с позициями
//                .filter(SalesOrder.class,
//                        order -> order.items() != null && !order.items().isEmpty())
                .channel(invoiceOutput())
                .get();
    }


}
