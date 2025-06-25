
package com.example.productservice.controller;

import com.example.productservice.service.ProductService;
import com.example.productservice.domain.Product;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public record ProductController(ProductService svc) {

    @PreAuthorize("hasRole('PRODUCT_ADMIN')")
    @GetMapping
    public Page<Product> list(
            @RequestParam(required=false) String q,
            @RequestParam(required=false) String category,
            @RequestParam(required=false) BigDecimal minPrice,
            @RequestParam(required=false) BigDecimal maxPrice,
            @PageableDefault(size=10, sort="createdAt", direction = Sort.Direction.DESC) Pageable pg) {
        return svc.search(q, category, minPrice, maxPrice, pg);
    }
}
