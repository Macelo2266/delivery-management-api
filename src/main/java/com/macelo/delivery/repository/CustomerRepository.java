package com.macelo.delivery.repository;

import com.macelo.delivery.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository  extends JpaRepository<Customer, Long> {

    boolean existsByDocument(String document);
}
