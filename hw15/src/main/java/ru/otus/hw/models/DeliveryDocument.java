package ru.otus.hw.models;

import java.util.List;

public record DeliveryDocument(String orderId, List<OrderItemDocument> items) {
}
