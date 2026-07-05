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
 * Form fields for multipart add product (binds from form-data).
 */
public class AddProductFormRequest {

    @NotBlank(message = "Product name is required")
    @Size(max = 200)
    private String productName;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    private BigDecimal amount;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    @NotNull(message = "Quantity type is required (KG or QTY)")
    private QuantityType quantityType;

    @DecimalMin(value = "0", message = "Parcel amount cannot be negative")
    private BigDecimal parcelAmount;

    @NotNull(message = "At least one food shift is required")
    @ValidFoodShifts
    private Set<FoodShift> foodShifts;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public QuantityType getQuantityType() {
        return quantityType;
    }

    public void setQuantityType(QuantityType quantityType) {
        this.quantityType = quantityType;
    }

    public BigDecimal getParcelAmount() {
        return parcelAmount;
    }

    public void setParcelAmount(BigDecimal parcelAmount) {
        this.parcelAmount = parcelAmount;
    }

    public Set<FoodShift> getFoodShifts() {
        return foodShifts;
    }

    public void setFoodShifts(Set<FoodShift> foodShifts) {
        this.foodShifts = foodShifts;
    }

    public AddProductRequest toRequest() {
        return new AddProductRequest(
                productName,
                amount,
                quantity,
                quantityType,
                parcelAmount,
                foodShifts
        );
    }
}
