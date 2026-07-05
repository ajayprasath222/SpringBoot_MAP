package com.example.printer_springbe.product.service;

import com.example.printer_springbe.auth.entity.User;
import com.example.printer_springbe.auth.model.AuthenticatedUser;
import com.example.printer_springbe.auth.repository.UserRepository;
import com.example.printer_springbe.common.exception.BusinessException;
import com.example.printer_springbe.common.response.ResponseCode;
import com.example.printer_springbe.product.dto.request.AddProductRequest;
import com.example.printer_springbe.product.dto.response.ProductResponse;
import com.example.printer_springbe.product.entity.Product;
import com.example.printer_springbe.product.enums.FoodShift;
import com.example.printer_springbe.product.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductImageStorageService imageStorageService;

    public ProductService(
            ProductRepository productRepository,
            UserRepository userRepository,
            ProductImageStorageService imageStorageService) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.imageStorageService = imageStorageService;
    }

    @Transactional
    public ProductResponse addProduct(AuthenticatedUser currentUser, AddProductRequest request, MultipartFile image) {
        User owner = userRepository.findById(currentUser.userId())
                .orElseThrow(() -> new BusinessException(
                        ResponseCode.UNAUTHENTICATED,
                        HttpStatus.UNAUTHORIZED,
                        "User account not found"
                ));

        Product product = new Product();
        product.setOwner(owner);
        product.setProductName(request.productName().trim());
        product.setAmount(request.amount());
        product.setQuantity(request.quantity());
        product.setQuantityType(request.quantityType());
        product.setParcelAmount(request.parcelAmount());
        product.setFoodShifts(new LinkedHashSet<>(request.foodShifts()));
        product.setImagePath(imageStorageService.store(image));

        Product saved = productRepository.save(product);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listMyProducts(AuthenticatedUser currentUser) {
        return productRepository.findByOwnerIdOrderByCreatedAtDesc(currentUser.userId()).stream()
                .map(this::toResponse)
                .toList();
    }

    private ProductResponse toResponse(Product product) {
        List<FoodShift> shifts = new ArrayList<>(product.getFoodShifts());
        return new ProductResponse(
                product.getId(),
                product.getOwner().getId(),
                imageStorageService.toPublicUrl(product.getImagePath()),
                product.getProductName(),
                product.getAmount(),
                product.getQuantity(),
                product.getQuantityType(),
                product.getParcelAmount(),
                shifts,
                product.getCreatedAt()
        );
    }
}
