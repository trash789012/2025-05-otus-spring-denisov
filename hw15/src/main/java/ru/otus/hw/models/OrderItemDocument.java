package ru.otus.hw.models;

public record OrderItemDocument(int deliveryId, int orderId, String item, double price) {
}
