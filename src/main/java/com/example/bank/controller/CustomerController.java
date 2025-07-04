package com.example.bank.controller;

import com.example.bank.dto.AccountDTO;
import com.example.bank.dto.CreateCustomerDTO;
import com.example.bank.dto.CustomerDTO;
import com.example.bank.dto.UpdateCustomerDTO;
import com.example.bank.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bank-management-system/customer")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerDTO> getCustomerDTOById(@PathVariable("customerId") UUID customerId){
        CustomerDTO customerDTO = customerService.getCustomerDTOById(customerId);
        if (customerDTO==null){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(customerDTO);
    }

    @GetMapping("/")
    public ResponseEntity<List<CustomerDTO>> getAllCustomer(){
        List<CustomerDTO> customerDTOList = customerService.getAllCustomer();
        if (customerDTOList==null || customerDTOList.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).body(customerDTOList);
    }

    @PostMapping("/create-customer")
    public ResponseEntity<CustomerDTO> createCustomer(@Valid @RequestBody CreateCustomerDTO createCustomerDTO){
        return ResponseEntity.ok(customerService.createCustomer(createCustomerDTO));
    }

    @PutMapping("/update-customer/{customerId}")
    public ResponseEntity<CustomerDTO>  updateCustomerDetails( @PathVariable("customerId") UUID customerId, @Valid @RequestBody UpdateCustomerDTO updateCustomerDTO){
        return ResponseEntity.status(HttpStatus.OK).body(customerService.updateCustomerDetails(customerId,updateCustomerDTO));
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<String> deleteCustomerById(@PathVariable("customerId") UUID customerId){
        customerService.deleteCustomer(customerId);
        return ResponseEntity.status(HttpStatus.OK).body("Customer deleted Successfully");
    }

    @GetMapping("/account/{customerId}")
    public ResponseEntity<List<AccountDTO>> getAllCustomerAccounts(@PathVariable("customerId") UUID customerId){
        return ResponseEntity.status(HttpStatus.OK).body(customerService.getCustomerAccounts(customerId));
    }
}
