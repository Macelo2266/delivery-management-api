package com.macelo.delivery.repository;

import com.macelo.delivery.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {

    boolean existsByTrackingCode(String trackingCode);

    Page<Order> findByCustomerId(Long customerId, Pageable pageable);
}