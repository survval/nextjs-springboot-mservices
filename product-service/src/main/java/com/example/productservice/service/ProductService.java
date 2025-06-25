package com.example.productservice.service;

import com.example.productservice.repo.ProductRepository;
import com.example.productservice.domain.Product;
import com.example.productservice.dto.ProductDTO;
import com.example.productservice.dto.ProductCreateRequest;
import com.example.productservice.dto.ProductUpdateRequest;
import com.example.productservice.filter.ProductPredicates;
import com.example.productservice.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;
    private final ProductMapper mapper;

    /**
     * Search for products with optional filtering.
     * Using var for local variables where type is obvious from the context.
     */
    @Transactional(readOnly = true)
    public Page<ProductDTO> search(String q, String category, BigDecimal min, BigDecimal max, Pageable pg) {
        var predicate = ProductPredicates.byFilter(q, category, min, max);
        var products = repo.findAll(predicate, pg);
        return products.map(mapper::toDto);
    }
    
    /**
     * Get a product by ID.
     */
    @Transactional(readOnly = true)
    public Optional<ProductDTO> findById(Long id) {
        return repo.findById(id).map(mapper::toDto);
    }
    
    /**
     * Create a new product.
     */
    @Transactional
    public ProductDTO create(ProductCreateRequest request) {
        var product = mapper.toEntity(request);
        var savedProduct = repo.save(product);
        return mapper.toDto(savedProduct);
    }
    
    /**
     * Update an existing product.
     */
    @Transactional
    public Optional<ProductDTO> update(Long id, ProductUpdateRequest request) {
        return repo.findById(id)
            .map(product -> {
                mapper.updateEntityFromRequest(request, product);
                return mapper.toDto(repo.save(product));
            });
    }
    
    /**
     * Delete a product by ID.
     */
    @Transactional
    public boolean delete(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
            return true;
        }
        return false;
    }
}