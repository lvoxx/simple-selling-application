package com.shitcode.demo1.service;

import com.shitcode.demo1.dto.SpringUserDTO;
import com.shitcode.demo1.entity.SpringUser;

public interface SpringUserService {
    SpringUserDTO.Response publicCreateUser(SpringUserDTO.UserRequest request);

    SpringUserDTO.Response privateCreateUser(SpringUserDTO.AdminRequest request);

    SpringUserDTO.Response findByEmailWithDTO(String username);

    SpringUser findByEmailWithoutDTO(String username);

    SpringUserDTO.Response publicUpdateUser(SpringUserDTO.UserRequest request, Long userId);

    SpringUserDTO.Response privateUpdateUser(SpringUserDTO.AdminRequest request, Long userId);

    SpringUserDTO.Response lockUser(Long userId);

    SpringUserDTO.Response disableUser(Long userId);
}