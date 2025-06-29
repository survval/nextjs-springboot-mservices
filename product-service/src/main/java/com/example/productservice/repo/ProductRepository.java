
package com.example.productservice.repo;

import com.example.productservice.domain.Product;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import com.querydsl.core.types.Predicate;

public interface ProductRepository extends JpaRepository<Product, Long>, QuerydslPredicateExecutor<Product> {}
