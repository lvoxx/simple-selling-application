package com.shitcode.demo1.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.shitcode.demo1.entity.SpringUser;
import com.shitcode.demo1.service.SpringUserService;

@Service
@Qualifier("springUserDetailsService")
public class SpringUserDetailsService implements UserDetailsService {

    private final SpringUserService service;

    public SpringUserDetailsService(SpringUserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SpringUser user = service.findByEmailWithoutDTO(email);
        return new SpringUserDetails(user);
    }

}