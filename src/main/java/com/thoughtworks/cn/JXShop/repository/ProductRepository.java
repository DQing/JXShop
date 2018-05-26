package com.thoughtworks.cn.JXShop.repository;

import com.thoughtworks.cn.JXShop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    List<Product> findByDescriptionContaining(String desc);
    List<Product> findByDescriptionContainingAndName(String desc, String name);
    List<Product> findByName(String name);
}
