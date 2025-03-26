package com.formation;

import com.formation.data.entity.CustomerEntity;
import com.formation.data.repository.CustomerRepository;
import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import com.sun.istack.NotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @InjectMocks
    CustomerService customerService;

    @Mock
    CustomerRepository customerRepository;

    @Test
    void getAllCustomers() {

        // Given
        Mockito.doReturn(getMockCustomers(2)).when(customerRepository).findAll();

        // When
        List<Customer> customers = customerService.getAllCustomers();

        // Then
        assertEquals(2, customers.size());
    }

    @Test
    void getCustomer() {
        CustomerEntity entity = getMockCustomerEntity();
        Optional<CustomerEntity> optional = Optional.of(entity);
        Mockito.doReturn(optional).when(customerRepository).findById(entity.getCustomerId());
        Customer customer = customerService.getCustomer(entity.getCustomerId().toString());
        assertNotNull(customer);
        assertEquals("testfirst", customer.getFirstName());
    }

    @Test
    void getCustomer_NotExists() {
        CustomerEntity entity = getMockCustomerEntity();
        Optional<CustomerEntity> optional = Optional.empty();
        Mockito.doReturn(optional).when(customerRepository).findById(entity.getCustomerId());
        assertThrows(NotFoundException.class, () ->customerService.getCustomer(entity.getCustomerId().toString()), "Exception not thrown as expected.");
    }

    @Test
    void findByEmailAddress() {
        CustomerEntity entity = getMockCustomerEntity();
        Mockito.doReturn(entity).when(customerRepository).findByEmailAddress(entity.getEmailAddress());
        Customer customer = customerService.findByEmailAddress(entity.getEmailAddress());
        assertNotNull(customer);
        assertEquals("testfirst", customer.getFirstName());
    }

    @Test
    void addCustomer () {
        CustomerEntity entity = getMockCustomerEntity();
        when(customerRepository.findByEmailAddress(entity.getEmailAddress())).thenReturn(null);
        when(customerRepository.save(any(CustomerEntity.class))).thenReturn(entity);
        Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(), entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
        customer = customerService.addCustomer(customer);
        assertNotNull(customer);
        assertEquals("testlast", customer.getLastName());
    }

    @Test
    void addCustomer_Existing() {
        CustomerEntity entity = getMockCustomerEntity();
        when(customerRepository.findByEmailAddress(entity.getEmailAddress())).thenReturn(entity);
        Customer customer = new Customer(entity.getCustomerId().toString(), entity.getFirstName(), entity.getLastName(), entity.getEmailAddress(), entity.getPhoneNumber(), entity.getAddress());
        assertThrows(ConflictException.class, () -> customerService.addCustomer(customer), "should have thrown conflict exception");
    }

    private CustomerEntity getMockCustomerEntity() {
        return new CustomerEntity(UUID.randomUUID(), "testfirst", "testlast", "testemail", "0000000", "rue de Paris");
    }

    @NotNull
    private Iterable<CustomerEntity> getMockCustomers(int size) {
        List<CustomerEntity> customers = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            customers.add(new CustomerEntity(UUID.randomUUID(), "firstname"+i, "lastname"+i, "email"+i, "phone"+i, "address"+i));
        }
        return customers;
    }
}
