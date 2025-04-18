package com.shitcode.demo1.service;

import com.shitcode.demo1.dto.SpringUserDTO;
import com.shitcode.demo1.entity.SpringUser;

public interface SpringUserService {

    SpringUserDTO.Response createUser(SpringUser request, boolean isValidEmail);

    SpringUserDTO.Response findByEmailWithDTO(String username);

    SpringUser findByEmailWithoutDTO(String username);

    SpringUserDTO.Response publicUpdateUser(SpringUserDTO.UserRequest request, Long userId);

    SpringUserDTO.Response privateUpdateUser(SpringUserDTO.AdminRequest request, Long userId);

    SpringUserDTO.Response lockOrNotUser(Long userId, boolean isLocked);

    SpringUserDTO.Response disableOrNotUser(Long userId, boolean isDisabled);

    SpringUserDTO.Response lockOrNotUser(String email, boolean isLocked);

    SpringUserDTO.Response disableOrNotUser(String email, boolean isDisabled);
}