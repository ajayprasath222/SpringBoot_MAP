package com.example.printer_springbe.product.dto.response;

import com.example.printer_springbe.product.enums.FoodShift;
import com.example.printer_springbe.product.enums.QuantityType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record ProductResponse(
        Long id,
        Long userId,
        String imageUrl,
        String productName,
        BigDecimal amount,
        BigDecimal quantity,
        QuantityType quantityType,
        BigDecimal parcelAmount,
        List<FoodShift> foodShifts,
        Instant createdAt
) {
}
