package com.macelo.delivery.repository;

import com.macelo.delivery.entity.DeliveryStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeliveryStatusHistoryRepository extends JpaRepository<DeliveryStatusHistory, Long> {

    List<DeliveryStatusHistory> findByOrderIdOrderByChangedAtAsc(Long orderId);
}
