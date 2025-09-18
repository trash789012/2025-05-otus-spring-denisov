package ru.otus.hw.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.AppRunner;
import ru.otus.hw.models.InvoiceDocument;
import ru.otus.hw.models.SalesOrder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@SpringIntegrationTest
public class SalesOrdersFlowTest {

    @MockitoBean
    private AppRunner appRunner;

    @Autowired
    private TestDocumentGateway documentGateway;

    private SalesOrder order1;
    private SalesOrder order2;
    private SalesOrder emptyOrder;

    @BeforeEach
    void setup() {
        order1 = new SalesOrder(1, List.of("Item A", "Item B"));
        order2 = new SalesOrder(2, List.of("Item C"));
        emptyOrder = new SalesOrder(3, List.of()); // пустой заказ для фильтрации
    }

    @Test
    @DisplayName("Должен создавать инвойс по списку заказов")
    public void shouldCreatesInvoiceFromOrders() {
        InvoiceDocument invoice = documentGateway.process(List.of(order1, emptyOrder, order2));

        assertThat(invoice).isNotNull();

        assertThat(invoice.totalPrice()).isPositive();
        assertThat(invoice.invoiceId()).isPositive();
    }

    @Test
    @DisplayName("Должен фильтровать заказы с пустыми артикулами")
    public void shouldFiltersEmptyOrders() {
        InvoiceDocument invoice = documentGateway.process(List.of(emptyOrder));

        assertThat(invoice).isNull();

    }

    @Test
    @DisplayName("Должен создавать инвойс с одним заказом")
    public void shouldCreateWithSingleOrders() {
        InvoiceDocument invoice = documentGateway.process(List.of(order1));

        assertThat(invoice).isNotNull();
        assertThat(invoice.totalPrice()).isPositive();
        assertThat(invoice.invoiceId()).isPositive();
    }
}
