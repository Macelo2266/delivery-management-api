package com.macelo.delivery.service;

import com.macelo.delivery.dto.request.CustomerRequest;
import com.macelo.delivery.dto.response.CustomerResponse;
import com.macelo.delivery.entity.Customer;
import com.macelo.delivery.exception.BusinessException;
import com.macelo.delivery.exception.ResourceNotFoundException;
import com.macelo.delivery.mapper.CustomerMapper;
import com.macelo.delivery.repository.CustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponse create(CustomerRequest request) {
        if (customerRepository.existsByDocument(request.getDocument())) {
            throw new BusinessException("Documento já cadastrado: " + request.getDocument());
        }
        Customer customer = customerMapper.toEntity(request);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    public Page<CustomerResponse> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(CustomerResponse::from);
    }

    public CustomerResponse findById(Long id) {
        return CustomerResponse.from(getCustomerOrThrow(id));
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = getCustomerOrThrow(id);

        boolean documentChanged = !customer.getDocument().equals(request.getDocument());
        if (documentChanged && customerRepository.existsByDocument(request.getDocument())) {
            throw new BusinessException("Documento já cadastrado: " + request.getDocument());
        }

        customerMapper.updateEntity(customer, request);
        return CustomerResponse.from(customer);
    }

    @Transactional
    public void delete(Long id) {
        Customer customer = getCustomerOrThrow(id);
        customerRepository.delete(customer);
    }

    private Customer getCustomerOrThrow(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado: id " + id));
    }
}
