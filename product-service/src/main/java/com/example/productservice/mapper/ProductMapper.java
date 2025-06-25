package com.example.productservice.mapper;

import com.example.productservice.domain.Product;
import com.example.productservice.dto.ProductDTO;
import com.example.productservice.dto.ProductCreateRequest;
import com.example.productservice.dto.ProductUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between {@link Product} and DTOs.
 */
@Mapper(config = BaseMapperConfig.class)
public interface ProductMapper {

    /**
     * Convert a Product entity to a ProductDTO.
     *
     * @param product the entity to convert
     * @return the DTO
     */
    ProductDTO toDto(Product product);

    /**
     * Convert a ProductCreateRequest to a Product entity.
     *
     * @param request the request to convert
     * @return the entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Product toEntity(ProductCreateRequest request);

    /**
     * Update a Product entity with data from a ProductUpdateRequest.
     * <p>
     * This method will not update the id or createdAt fields.
     *
     * @param request the source request
     * @param product the target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromRequest(ProductUpdateRequest request, @MappingTarget Product product);
}
