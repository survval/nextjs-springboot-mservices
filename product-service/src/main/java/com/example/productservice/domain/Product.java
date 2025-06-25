
package com.example.productservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name="products")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
    private Instant createdAt;

    @PrePersist
    void onCreate(){ createdAt = Instant.now(); }
}
