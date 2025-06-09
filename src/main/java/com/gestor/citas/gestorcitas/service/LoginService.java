package com.gestor.citas.gestorcitas.service;

import com.gestor.citas.gestorcitas.entity.Role;
import com.gestor.citas.gestorcitas.entity.User;
import com.gestor.citas.gestorcitas.security.CustomUserDetails;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    public User getInfoUserLogged() {
        return ((CustomUserDetails)SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUser();
    }

    public boolean hasRole(String role) {
        User userLogged = ((CustomUserDetails)SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal()).getUser();

        for (Role roles : userLogged.getRoles()) {
            if (roles.getName().equals(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean isLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return !(authentication instanceof AnonymousAuthenticationToken);
    }

    public boolean hasRoleAdmin() {
        return hasRole("ROLE_ADMIN");
    }
}
