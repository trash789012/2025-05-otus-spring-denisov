package ru.otus.hw.models;

import java.util.List;

public record DeliveryDocument(int deliveryId, int orderId, List<OrderItemDocument> items) {
}
