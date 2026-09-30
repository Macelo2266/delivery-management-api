package com.macelo.delivery.repository.specification;

import com.macelo.delivery.dto.request.OrderFilter;
import com.macelo.delivery.entity.Order;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderSpecification {

    private OrderSpecification() {
        // classe utilitária — não deve ser instanciada
    }

    public static Specification<Order> withFilters(OrderFilter filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getTrackingCode() != null && !filter.getTrackingCode().isBlank()) {
                predicates.add(criteriaBuilder.equal(root.get("trackingCode"), filter.getTrackingCode()));
            }

            if (filter.getCustomerId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("customer").get("id"), filter.getCustomerId()));
            }

            if (filter.getDriverId() != null) {
                predicates.add(criteriaBuilder.equal(root.get("driver").get("id"), filter.getDriverId()));
            }

            if (filter.getCreatedAt() != null) {
                LocalDateTime startOfDay = filter.getCreatedAt().atStartOfDay();
                LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
                predicates.add(criteriaBuilder.between(root.get("createdAt"), startOfDay, endOfDay));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
}
