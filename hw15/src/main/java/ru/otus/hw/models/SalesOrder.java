package ru.otus.hw.models;

import java.util.List;

public record SalesOrder(int orderId, List<String> items) {
}
