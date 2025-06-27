package com.example.bank.service;

import com.example.bank.dto.AccountDTO;
import com.example.bank.dto.CreateCustomerDTO;
import com.example.bank.dto.CustomerDTO;
import com.example.bank.dto.UpdateCustomerDTO;
import com.example.bank.model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    CustomerDTO createCustomer(CreateCustomerDTO createCustomerDTO);

    CustomerDTO getCustomerDTOById(UUID customerId);

    List<CustomerDTO> getAllCustomer();

    CustomerDTO updateCustomerDetails(UUID customerId , UpdateCustomerDTO updateCustomerDTO);

    void deleteCustomer(UUID customerId);

    Customer findCustomerById(UUID customerId);

    List<AccountDTO> getCustomerAccounts(UUID customerId);
}
