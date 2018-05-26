package com.thoughtworks.cn.JXShop.repository;

import com.thoughtworks.cn.JXShop.entity.PurchaseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseItemRepository extends JpaRepository<PurchaseItem, Long> {
   List<PurchaseItem> findByOrderId(Long id);
}
