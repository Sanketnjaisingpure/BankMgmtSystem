package com.example.bank.serviceImpl;

import  com.example.bank.Enum.IdentityProofType;
import com.example.bank.dto.*;
import com.example.bank.model.*;
import com.example.bank.repository.CustomerRepository;
import com.example.bank.repository.UserDetailsRepo;
import com.example.bank.service.AccountService;
import com.example.bank.utils.IdentityValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    /*You’re telling Mockito:

    “Don’t use the real logic of IdentityValidator or any .
    Just give me a dummy object where I define the return values explicitly.”*/
    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IdentityValidator identityValidator;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private UserDetailsRepo userDetailsRepo;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private AccountService accountService;


    @InjectMocks
    private CustomerServiceImpl customerService;


    /*Create helper method */

    private AddressDTO validAddressDTO() {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet("123 Main St");
        addressDTO.setCity("Springfield");
        addressDTO.setState("IL");
        addressDTO.setZipCode("62701");
        addressDTO.setCountry("USA");
        return addressDTO;
    }

    private CreateCustomerDTO validDto(){
        CreateCustomerDTO dto = new CreateCustomerDTO();
        dto.setFirstName("John");
        dto.setMiddleName("Smith");
        dto.setLastName("Doe");
        dto.setEmail("john12@gmail.com");
        dto.setPhoneNumber("1234567890");
        dto.setDateOfBirth(LocalDate.parse("1990-01-01"));
        dto.setAddressDTO(validAddressDTO());
        dto.setPassword("password123");
        dto.setIdentityProofType(IdentityProofType.PASSPORT);
        dto.setIdentityProofId("A123456789");

        return dto;
    }

    @Test
    void create_customerSuccess(){
        CreateCustomerDTO dto = validDto();
        AddressDTO addressDTO = validAddressDTO();

        // validate Names
        when(identityValidator.nameValidate(dto.getFirstName())).thenReturn(true);
        when(identityValidator.nameValidate(dto.getMiddleName())).thenReturn(true);
        when(identityValidator.nameValidate(dto.getLastName())).thenReturn(true);

        when(identityValidator.isValid(dto.getIdentityProofType() , dto.getIdentityProofId())).thenReturn(true);

        // checking whether email , phone number, identity proof type and id already exists
        when(customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(dto.getEmail(),
                dto.getPhoneNumber(), dto.getIdentityProofType(), dto.getIdentityProofId())).thenReturn(false);

        when(userDetailsRepo.findByUsername(dto.getEmail())).thenReturn(null);

        // encoding password
        when(encoder.encode(dto.getPassword())).thenReturn("encodedPassword");

        when(modelMapper.map(dto.getAddressDTO(), Address.class)).thenReturn(new Address());

        Customer savedCustomer = new Customer();
        UUID fakedUUID = UUID.randomUUID();
        savedCustomer.setCustomerId(fakedUUID);
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);


        // Return DTO
        CustomerDTO expected = new CustomerDTO();
        expected.setAddressDTO(addressDTO);
        when(modelMapper.map(dto, CustomerDTO.class)).thenReturn(expected);

        // Actual method call
        CustomerDTO actual = customerService.createCustomer(dto);
        
        // then verify
        assertSame(expected, actual);
        verify(customerRepository).save(any(Customer.class));
        verify(userDetailsRepo).save(any(Users.class));

    }


    @Test
    void create_customer_inValidName(){
        CreateCustomerDTO dto = validDto();
        dto.setFirstName("3234~~");
        dto.setMiddleName("234234234");
        when(identityValidator.nameValidate(dto.getFirstName())).thenReturn(false);
        // it is expected to throw a RuntimeException
        // when customerService.createCustomer(dto) is called
        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(dto));
        assertEquals("Invalid name format", ex.getMessage());

    }

    @Test
    void create_customer_inValidIdentityProof(){
        CreateCustomerDTO dto = validDto();
        dto.setIdentityProofId("!!!!!!!");
        dto.setIdentityProofType(IdentityProofType.PASSPORT);

        // why I need to mock this?
        // because mockito by default will take name as dummy value, and service method can return false,
        // hence retuning invalid name format , hence we need to mock it
        when(identityValidator.nameValidate(dto.getFirstName())).thenReturn(true);
        when(identityValidator.nameValidate(dto.getMiddleName())).thenReturn(true);
        when(identityValidator.nameValidate(dto.getLastName())).thenReturn(true);

        when(identityValidator.isValid(dto.getIdentityProofType(), dto.getIdentityProofId())).thenReturn(false);
        // it is expected to throw a RuntimeException
        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(dto));
        assertEquals("Invalid identity proof type or id", ex.getMessage());
    }

    @Test
    void create_customer_futureDateOfBirth() {
        CreateCustomerDTO dto = validDto();
        dto.setDateOfBirth(LocalDate.now().plusDays(1));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(dto));
        assertEquals("Date of Birth cannot be in future", ex.getMessage());
    }

    @Test
    void getCustomerDTOById() {

        UUID fakeId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setCustomerId(fakeId);

        CustomerDTO customerDTO = new CustomerDTO();

        AddressDTO addressDTO  = new AddressDTO();

        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(customerDTO);
        when(modelMapper.map(customer.getAddress(), AddressDTO.class)).thenReturn(addressDTO);
        // Then call actual method
        CustomerDTO result = customerService.getCustomerDTOById(customer.getCustomerId());
        result.setAddressDTO(addressDTO);
        assertNotNull(result);

    }

    @Test
    void findCustomerById() {
        UUID fakeId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setCustomerId(fakeId);

        when(customerRepository.findById(fakeId)).thenReturn(Optional.of(customer));
        Customer result = customerService.findCustomerById(fakeId);
        assertNotNull(result);
    }

    @Test
    void getAllCustomer() {
        Customer customer1 = new Customer();
        Customer customer2 = new Customer();
        when(customerRepository.findAll()).thenReturn(List.of(customer2, customer1));
        List<CustomerDTO> result = customerService.getAllCustomer();
        assertNotNull(result);

    }

    @Test
    void updateCustomerDetails() {

        UUID fakeId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setCustomerId(fakeId);

        Address address = new Address();
        customer.setAddress(address);

        AddressDTO addressDTO = new AddressDTO();
        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));

        UpdateCustomerDTO updateCustomerDTO = new UpdateCustomerDTO();
        updateCustomerDTO.setAddressDTO(addressDTO);

        CustomerDTO customerDTO = new CustomerDTO();

        when(customerRepository.save(customer)).thenReturn(customer);
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(new CustomerDTO());
        when(customerService.updateCustomerDetails(customer.getCustomerId(), updateCustomerDTO)).thenReturn(customerDTO);

        CustomerDTO result = customerService.updateCustomerDetails(customer.getCustomerId(), updateCustomerDTO);

        assertNotNull(result);

    }

    @Test
    void deleteCustomer() {

        UUID fakeId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setCustomerId(fakeId);

        when(customerRepository.findById(fakeId)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).delete(customer);

        customerService.deleteCustomer(fakeId);

        verify(customerRepository, times(1)).delete(customer);
    }

    @Test
    void getCustomerAccounts() {

        UUID fakeId = UUID.randomUUID();

//        when(maskedNumber.maskNumber(fakeId.toString())).thenReturn("2u398u2398");

        Customer customer = new Customer();
        customer.setCustomerId(fakeId);
        customer.setFirstName("John");
        customer.setLastName("Doe");
        customer.setMiddleName("Smith");

        Bank bank = new Bank();
        bank.setBankName("Test Bank");

        Branch branch = new Branch();
        branch.setBranchName("Test Branch");
        branch.setBank(bank);

        Account account = new Account();
        account.setAccountId(UUID.randomUUID());
        account.setAccountNumber("1234567890");
        account.setCustomer(customer);
        account.setBranch(branch);

        List<Account> accounts =  accountService.FindAllCustomerAccountByCustomerId(fakeId);

        assertNotNull(accounts);

    }
}