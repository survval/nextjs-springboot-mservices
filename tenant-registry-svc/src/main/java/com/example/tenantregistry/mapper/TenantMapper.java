package com.example.tenantregistry.mapper;

import com.example.tenantregistry.domain.Tenant;
import com.example.tenantregistry.dto.TenantDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for converting between {@link Tenant} and {@link TenantDTO}.
 */
@Mapper(config = MapperConfig.class)
public interface TenantMapper {

    /**
     * Convert a Tenant entity to a TenantDTO.
     *
     * @param tenant the entity to convert
     * @return the DTO
     */
    TenantDTO toDto(Tenant tenant);

    /**
     * Convert a TenantDTO to a Tenant entity.
     *
     * @param dto the DTO to convert
     * @return the entity
     */
    Tenant toEntity(TenantDTO dto);

    /**
     * Update a Tenant entity with data from a TenantDTO.
     * <p>
     * This method will not update the id, createdAt, or updatedAt fields.
     *
     * @param dto the source DTO
     * @param entity the target entity to update
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDto(TenantDTO dto, @MappingTarget Tenant entity);
}
