package com.thoughtworks.cn.JXShop.repository;

import com.thoughtworks.cn.JXShop.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Inventory save(Inventory inventory);
    Optional<Inventory> findByProductId(Long id);
}
