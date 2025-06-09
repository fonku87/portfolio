package com.gestor.citas.gestorcitas.service;

import com.gestor.citas.gestorcitas.entity.User;
import com.gestor.citas.gestorcitas.entity.Role;
import com.gestor.citas.gestorcitas.repository.RoleRepository;
import com.gestor.citas.gestorcitas.repository.UserRepository;
import com.gestor.citas.gestorcitas.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new CustomUserDetails(user);
    }
}