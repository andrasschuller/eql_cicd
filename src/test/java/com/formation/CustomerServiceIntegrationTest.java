package com.formation;

import com.formation.service.CustomerService;
import com.formation.web.error.ConflictException;
import com.formation.web.error.NotFoundException;
import com.formation.web.model.Customer;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CustomerServiceIntegrationTest {

    @Autowired
    CustomerService customerService;

    @Test
    void getAllCustomers(){
        List<Customer> customers = customerService.getAllCustomers();

        assertEquals(5, customers.size());
    }

    @Test
    void getCustomer() {
        Customer customer = this.customerService.getCustomer("054b145c-ddbc-4136-a2bd-7bf45ed1bef7");
        assertNotNull(customer);
        assertEquals("Cally", customer.getFirstName());
    }

    @Test
    void getCostumerNotFound () {
        assertThrows(NotFoundException.class, () -> this.customerService.getCustomer("972b30f-21cc-4" +
                "11f-b374-685ce23cd317"), "should have thrown an exception");
    }

    @Test
    void addCustomerAlreadyExists () {
        Customer customer = new Customer("", "John", "Doe", "penatibus.et@lectusa.com", "555-515-1234", "1234 Main Street; Anytown, KS 66110");
        assertThrows(ConflictException.class, () -> this.customerService.addCustomer(customer));
    }

    @Test
    void updateCostumer () {
        Customer customer = new Customer("", "John", "Doe", "jdoe@test.com", "555-515-1234", "1234 Main Street; Anytown, KS 66110");
        customer = this.customerService.addCustomer(customer);
        customer.setFirstName("Jane");
        customer = this.customerService.addCustomer(customer);
        assertEquals("Jane", customer.getFirstName());
        this.customerService.deleteCustomer(customer.getCustomerId());
    }
}
