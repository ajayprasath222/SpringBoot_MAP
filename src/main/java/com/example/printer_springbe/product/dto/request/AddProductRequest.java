package com.example.printer_springbe.product.dto.request;

import com.example.printer_springbe.product.enums.FoodShift;
import com.example.printer_springbe.product.enums.QuantityType;
import com.example.printer_springbe.product.validation.ValidFoodShifts;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.Set;

/**
 * JSON body for add product (no image file). Use multipart endpoint to upload an image.
 */
public record AddProductRequest(
        @NotBlank(message = "Product name is required")
        @Size(max = 200, message = "Product name must be at most 200 characters")
        String productName,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be greater than zero")
        BigDecimal quantity,

        @NotNull(message = "Quantity type is required (KG or QTY)")
        QuantityType quantityType,

        @DecimalMin(value = "0", message = "Parcel amount cannot be negative")
        BigDecimal parcelAmount,

        @NotNull(message = "At least one food shift is required")
        @ValidFoodShifts
        Set<FoodShift> foodShifts
) {
}
