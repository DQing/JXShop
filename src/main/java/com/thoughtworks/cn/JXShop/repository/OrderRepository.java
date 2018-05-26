package com.thoughtworks.cn.JXShop.repository;

import com.thoughtworks.cn.JXShop.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUserId(Integer userId);
    Order findByLogisticsRecordId(Long id);
    Optional<Order> findById(Long id);
}
