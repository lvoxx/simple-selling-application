package com.shitcode.demo1.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

import com.shitcode.demo1.dto.SpringUserDTO;
import com.shitcode.demo1.entity.SpringUser;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = false))
public interface SpringUserMapper {
        SpringUserMapper INSTANCE = Mappers.getMapper(SpringUserMapper.class);

        // Map SpringUserRegisterRequest to SpringUser
        @Mappings({
                        @Mapping(target = "id", ignore = true),
                        @Mapping(target = "enabled", ignore = true),
                        @Mapping(target = "locked", ignore = true),
                        @Mapping(target = "points", ignore = true),
                        @Mapping(target = "roles", ignore = true)
        })
        SpringUser toSpringUser(SpringUserDTO.UserRequest request);

        // Map SpringUserAdminequest to SpringUser
        @Mappings({
                        @Mapping(target = "id", ignore = true)
        })
        SpringUser toSpringUser(SpringUserDTO.AdminRequest request);

        // Map SpringUser to CustomerResponse
        SpringUserDTO.Response toSpringUserResponse(SpringUser user);

}