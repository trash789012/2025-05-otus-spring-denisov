package ru.otus.hw.models;

import java.math.BigDecimal;

public record InvoiceDocument(String orderId, BigDecimal totalPrice) {
}
