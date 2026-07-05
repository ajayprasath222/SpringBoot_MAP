package com.example.printer_springbe.product.controller;

import com.example.printer_springbe.auth.model.AuthenticatedUser;
import com.example.printer_springbe.auth.web.CurrentUser;
import com.example.printer_springbe.common.response.ApiResponse;
import com.example.printer_springbe.common.response.ApiResponses;
import com.example.printer_springbe.product.dto.request.AddProductFormRequest;
import com.example.printer_springbe.product.dto.request.AddProductRequest;
import com.example.printer_springbe.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/products")
@Validated
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Add product (JSON). URL must be: POST http://localhost:8080/api/v1/products/add
     */
    @PostMapping(path = "/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse> addProductJson(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @RequestBody AddProductRequest request) {
        return ApiResponses.okEntity("AddProduct", productService.addProduct(currentUser, request, null));
    }

    /**
     * Add product with optional image (multipart/form-data).
     */
    @PostMapping(path = "/add-with-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse> addProductMultipart(
            @CurrentUser AuthenticatedUser currentUser,
            @Valid @ModelAttribute AddProductFormRequest form,
            @RequestPart(value = "image", required = false) MultipartFile image) {
        return ApiResponses.okEntity(
                "AddProduct",
                productService.addProduct(currentUser, form.toRequest(), image)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse> listMyProducts(@CurrentUser AuthenticatedUser currentUser) {
        return ApiResponses.okEntity("MyProducts", productService.listMyProducts(currentUser));
    }
}
