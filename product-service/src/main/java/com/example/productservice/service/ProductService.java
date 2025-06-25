
package com.example.productservice.service;

import com.example.productservice.repo.ProductRepository;
import com.example.productservice.domain.Product;
import com.example.productservice.filter.ProductPredicates;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repo;

    public Page<Product> search(String q, String category, BigDecimal min, BigDecimal max, Pageable pg){
        return repo.findAll(ProductPredicates.byFilter(q, category, min, max), pg);
    }
}
