package com.gestor.citas.gestorcitas.repository;

import com.gestor.citas.gestorcitas.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Product findById(long id);

    Product findByName(String name);

    boolean existsByName(String name);

    Product findByNameEqualsAndIdNot(String name, Long id);
}
