package com.example.printer_springbe.product.repository;

import com.example.printer_springbe.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByOwnerIdOrderByCreatedAtDesc(Long ownerId);
}
