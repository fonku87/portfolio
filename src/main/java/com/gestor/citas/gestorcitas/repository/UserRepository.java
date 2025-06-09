package com.gestor.citas.gestorcitas.repository;

import com.gestor.citas.gestorcitas.entity.Role;
import com.gestor.citas.gestorcitas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/*public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    public User findByEmail(String email);

}*/
public interface UserRepository extends JpaRepository<User, Long> {

    User findById(long id);

    User findByEmail(String email);

    User findByPhone(String phone);

    List<User> findAllByRolesNot(Role role);

}