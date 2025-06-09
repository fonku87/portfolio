package com.gestor.citas.gestorcitas.repository;

import com.gestor.citas.gestorcitas.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByName(String name);

}
