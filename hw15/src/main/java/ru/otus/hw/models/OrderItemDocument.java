package ru.otus.hw.models;

import java.math.BigDecimal;

public record OrderItemDocument(String orderId, BigDecimal price, String currency) {
}
