package com.example.productservice.controller;

import com.example.productservice.service.ProductService;
import com.example.productservice.dto.ProductDTO;
import com.example.productservice.dto.ProductCreateRequest;
import com.example.productservice.dto.ProductUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import java.math.BigDecimal;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

/**
 * REST controller for Product operations.
 * Using Java 17 record for concise controller implementation.
 */
@RestController
@RequestMapping("/products")
public record ProductController(ProductService svc) {

    /**
     * List products with optional filtering and pagination.
     */
    @PreAuthorize("hasRole('PRODUCT_ADMIN')")
    @GetMapping
    public Page<ProductDTO> list(
            @RequestParam(required=false) String q,
            @RequestParam(required=false) String category,
            @RequestParam(required=false) BigDecimal minPrice,
            @RequestParam(required=false) BigDecimal maxPrice,
            @PageableDefault(size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pg) {
        return svc.search(q, category, minPrice, maxPrice, pg);
    }
    
    /**
     * Get a product by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        return svc.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Create a new product.
     */
    @PreAuthorize("hasRole('PRODUCT_ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDTO create(@Valid @RequestBody ProductCreateRequest request) {
        return svc.create(request);
    }
    
    /**
     * Update an existing product.
     */
    @PreAuthorize("hasRole('PRODUCT_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductUpdateRequest request) {
        return svc.update(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Delete a product by ID.
     */
    @PreAuthorize("hasRole('PRODUCT_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return svc.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}