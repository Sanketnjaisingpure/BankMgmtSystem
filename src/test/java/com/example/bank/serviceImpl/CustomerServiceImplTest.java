package com.example.bank.serviceImpl;

import com.example.bank.Enum.CardType;
import  com.example.bank.Enum.IdentityProofType;
import com.example.bank.Enum.LoanStatus;
import com.example.bank.dto.*;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.*;
import com.example.bank.repository.CustomerRepository;
import com.example.bank.repository.UserDetailsRepo;
import com.example.bank.service.AccountService;
import com.example.bank.utils.IdentityValidator;
import com.example.bank.utils.MaskedNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
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

    @Mock
    private MaskedNumber maskedNumber;

    @InjectMocks
    private CustomerServiceImpl customerService;


    private Customer testCustomer;
    private Address testAddress;
    private AddressDTO addressDTO;
    private Branch testBranch;
    private CreateCustomerDTO dto;
    private Bank testBank;
    private UpdateCustomerDTO updateCustomerDTO;
    private Loan testLoan;
    private Card testCard;

    @BeforeEach
    void setUp() {
        // This method is called before each test to set up the mocks and inject them into the service

        addressDTO = new AddressDTO();
        addressDTO.setStreet("123 Main St");
        addressDTO.setCity("Springfield");
        addressDTO.setState("IL");
        addressDTO.setZipCode("62701");
        addressDTO.setCountry("USA");

        dto = new CreateCustomerDTO();
        dto.setFirstName("John");
        dto.setMiddleName("Smith");
        dto.setLastName("Doe");
        dto.setEmail("john12@gmail.com");
        dto.setPhoneNumber("1234567890");
        dto.setDateOfBirth(LocalDate.parse("1990-01-01"));
        dto.setAddressDTO(addressDTO);
        dto.setPassword("password123");
        dto.setIdentityProofType(IdentityProofType.PASSPORT);
        dto.setIdentityProofId("A123456789");

        testAddress = new Address();
        testAddress.setStreet("123 Main St");
        testAddress.setCity("Springfield");
        testAddress.setState("IL");
        testAddress.setZipCode("627012");
        testAddress.setCountry("USA");



       testCustomer = new Customer();
        testCustomer.setCustomerId(UUID.randomUUID());
        testCustomer.setFirstName("John");
        testCustomer.setMiddleName("Smith");
        testCustomer.setLastName("Doe");
        testCustomer.setEmail("john@gmail.com");
        testCustomer.setPhoneNumber("1234567890");
        testCustomer.setDateOfBirth(LocalDate.parse("1990-01-01"));;
        testCustomer.setAddress(testAddress);
        testCustomer.setPassword("password123");
        testCustomer.setIdentityProofType(IdentityProofType.PASSPORT);
        testCustomer.setIdentityProofId("A123456789");
        testCustomer.setLoanList(List.of(new Loan()));

        testBank = new Bank();
        testBank.setBankId(UUID.randomUUID());
        testBank.setBankName("Test Bank");

        testBranch = new Branch();
        testBranch.setBranchId(1L);
        testBranch.setBank(testBank);
        testBranch.setBranchName("Main Branch");
        testBranch.setIfscCode("TREE12345");
        testBranch.setAddress(testAddress);


        updateCustomerDTO = new UpdateCustomerDTO();
        updateCustomerDTO.setAddressDTO(addressDTO);
        updateCustomerDTO.setEmail("jane@gmail.com");
        updateCustomerDTO.setPhoneNumber("0987654321");
        updateCustomerDTO.setIdentityProofType(IdentityProofType.PASSPORT);
        updateCustomerDTO.setIdentityProofId("B987654321");

        testLoan = new Loan();
        testLoan.setLoanId(UUID.randomUUID());

        testCustomer.setLoanList(List.of(testLoan));

        testCard = new Card();
        testCard.setCustomer(testCustomer);
        testCard.setCardNumber(1234567890L);
        testCard.setCardType(CardType.CREDIT);

    }

    @Test
    void findCustomerById_Success(){

        Customer customer = testCustomer;
        customer.setCustomerId(UUID.randomUUID());

        when(maskedNumber.maskNumber(anyString())).thenReturn("*****67890");
        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));

        Customer foundCustomer = customerService.findCustomerById(customer.getCustomerId());
        assertNotNull(foundCustomer);
        assertEquals(customer.getCustomerId(), foundCustomer.getCustomerId());
    }

    @Test
    void findCustomerById_ThrowsException() {
        UUID customerId = UUID.randomUUID();

        when(maskedNumber.maskNumber(anyString())).thenReturn("*****67890");
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> customerService.findCustomerById(customerId));

        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void getCustomerDTOById_Success() {
        Customer customer = testCustomer;
        customer.setCustomerId(UUID.randomUUID());

        CustomerDTO customerDTO = new CustomerDTO();

        when(maskedNumber.maskNumber(anyString())).thenReturn("*****67890");
        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));
        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(customerDTO);
        when(modelMapper.map(customer.getAddress(), AddressDTO.class)).thenReturn(addressDTO);
        CustomerDTO foundCustomerDTO = customerService.getCustomerDTOById(customer.getCustomerId());
        foundCustomerDTO.setAddressDTO(addressDTO);
        assertNotNull(foundCustomerDTO);

    }

    @Test
    void getAllCustomer_Success() {
        Customer customer1 = testCustomer;
        customer1.setCustomerId(UUID.randomUUID());

        Customer customer2 = testCustomer;
        customer2.setCustomerId(UUID.randomUUID());

        List<Customer> customers = List.of(customer1, customer2);

        when(modelMapper.map(customer1, CustomerDTO.class)).thenReturn(new CustomerDTO());
        when(modelMapper.map(customer2, CustomerDTO.class)).thenReturn(new CustomerDTO());

        when(modelMapper.map(customer1.getAddress(), AddressDTO.class)).thenReturn(new AddressDTO());
        when(modelMapper.map(customer2.getAddress(), AddressDTO.class)).thenReturn(new AddressDTO());

        List<CustomerDTO> customerDTOs = new ArrayList<>();
        customers.forEach(customer -> {
            CustomerDTO customerDTO = modelMapper.map(customer, CustomerDTO.class);
            customerDTO.setAddressDTO(modelMapper.map(customer.getAddress(), AddressDTO.class));
            customerDTOs.add(customerDTO);
        });

       assertNotNull(customerDTOs);
    }

    @Test
    void getAllCustomer_EmptyList() {
        when(customerRepository.findAll()).thenReturn(new ArrayList<>());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> customerService.getAllCustomer());

        assertEquals("Customers not found", ex.getMessage());

    }

    @Test
    void getCustomerAccounts_Success() {
        Customer customer = testCustomer;
        customer.setCustomerId(UUID.randomUUID());



        Account account1 = new Account();
        account1.setAccountId(UUID.randomUUID());
        account1.setAccountNumber("1234567890");
        account1.setBalance(BigDecimal.valueOf(1000.0));
        account1.setCustomer(customer);
        account1.setBranch(testBranch);

        Account account2 = new Account();
        account2.setAccountId(UUID.randomUUID());
        account2.setAccountNumber("0987654321");
        account2.setBalance(BigDecimal.valueOf(2000.0));
        account2.setCustomer(customer);
        account2.setBranch(testBranch);

        List<Account> accounts = List.of(account1, account2);

        when(maskedNumber.maskNumber(anyString())).thenReturn("*****67890");

        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));

        when(accountService.FindAllCustomerAccountByCustomerId(customer.getCustomerId())).thenReturn(accounts);

        when(modelMapper.map(account1, AccountDTO.class)).thenReturn(new AccountDTO());
        when(modelMapper.map(account2, AccountDTO.class)).thenReturn(new AccountDTO());


        List<AccountDTO> accountDTOs = customerService.getCustomerAccounts(customer.getCustomerId());

        assertNotNull(accountDTOs);
        assertEquals(2, accountDTOs.size());
    }

    @Test
    void updateCustomer_Success() {
        Customer customer = testCustomer;
        customer.setCustomerId(UUID.randomUUID());

        when(maskedNumber.maskNumber(anyString())).thenReturn("*****67890");

        when(modelMapper.map(customer, CustomerDTO.class)).thenReturn(new CustomerDTO());

        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));

        when(customerService.updateCustomerDetails(customer.getCustomerId(), updateCustomerDTO)).thenReturn(new CustomerDTO());

        CustomerDTO updatedCustomerDTO = customerService.updateCustomerDetails(customer.getCustomerId(), updateCustomerDTO);

        assertNotNull(updatedCustomerDTO);

    }

    @Test
    void deleteCustomer_Success(){

        Customer customer = testCustomer;
        customer.setCustomerId(UUID.randomUUID());

        testLoan = new Loan();
        testLoan.setLoanStatus(LoanStatus.PENDING);
        testLoan.setCustomer(customer);
        customer.setLoanList(List.of(testLoan));

        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));

        when(maskedNumber.maskNumber(anyString())).thenReturn("*****67890");

        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));



        doNothing().when(customerRepository).delete(customer);

        customerService.deleteCustomer(customer.getCustomerId());

        verify(customerRepository, times(1)).delete(customer);

    }

    @Test
    void deleteCustomer_ThrowsException_ActiveLoan(){
        Customer customer = testCustomer;
        customer.setCustomerId(UUID.randomUUID());

        testLoan = new Loan();
        testLoan.setLoanStatus(LoanStatus.ACTIVE);
        testLoan.setCustomer(customer);
        customer.setLoanList(List.of(testLoan));

        when(maskedNumber.maskNumber(anyString())).thenReturn("*****678");

        when(customerRepository.findById(customer.getCustomerId())).thenReturn(Optional.of(customer));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> customerService.deleteCustomer(customer.getCustomerId()));

        assertEquals("Customer has active loan associated with it", ex.getMessage());
    }

    @Test
    void deleteCustomer_ThrowsException_CustomerNotFound() {
        UUID customerId = UUID.randomUUID();

        when(maskedNumber.maskNumber(anyString())).thenReturn("*****67890");
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> customerService.deleteCustomer(customerId));

        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void createCustomer_Success() {

        CreateCustomerDTO createCustomerDTO = dto;


        when(identityValidator.nameValidate(anyString())).thenReturn(true);

        when(customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(createCustomerDTO.getEmail(),
                createCustomerDTO.getPhoneNumber(),createCustomerDTO.getIdentityProofType(),createCustomerDTO.getIdentityProofId())).thenReturn(false);

        when(identityValidator.isValid(createCustomerDTO.getIdentityProofType(),createCustomerDTO.getIdentityProofId())).thenReturn(true);
        when(userDetailsRepo.findByUsername(createCustomerDTO.getEmail())).thenReturn(null);

        when(modelMapper.map(createCustomerDTO.getAddressDTO(), Address.class)).thenReturn(testAddress);

        testCustomer.setAddress(testAddress);
        Users user = new Users();
        user.setUsername(createCustomerDTO.getEmail());
        when(encoder.encode(anyString())).thenReturn("encodedPassword");

        when(userDetailsRepo.save(any(Users.class))).thenReturn(new Users());
        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);
        when(maskedNumber.maskNumber(anyString())).thenReturn("*****67890");

        CustomerDTO expectedCustomerDTO = new CustomerDTO();
        expectedCustomerDTO.setFirstName("John");

        when(modelMapper.map(createCustomerDTO, CustomerDTO.class)).thenReturn(expectedCustomerDTO);

        CustomerDTO customerDTO = customerService.createCustomer(createCustomerDTO);

        assertNotNull(customerDTO);

        assertEquals("John", customerDTO.getFirstName());

    }

    @Test
    void createCustomer_ThrowsException_EmailAlreadyExists() {
        CreateCustomerDTO createCustomerDTO = dto;
        createCustomerDTO.setEmail("john@gmail.com");

        when(identityValidator.nameValidate(anyString())).thenReturn(true);

        when(customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(
                createCustomerDTO.getEmail(),
                createCustomerDTO.getPhoneNumber(),
                createCustomerDTO.getIdentityProofType(),
                createCustomerDTO.getIdentityProofId()))
                .thenReturn(false);



        when(identityValidator.isValid(createCustomerDTO.getIdentityProofType(), createCustomerDTO.getIdentityProofId())).thenReturn(true);

        when(userDetailsRepo.findByUsername(createCustomerDTO.getEmail())).thenReturn(new Users());

        when(customerRepository.save(any(Customer.class))).thenReturn(testCustomer);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(dto));

        assertEquals("User with this email already exists", ex.getMessage());
    }

    @Test
    void createCustomer_ThrowsException_existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(){
        CreateCustomerDTO createCustomerDTO = dto;

        when(identityValidator.nameValidate(anyString())).thenReturn(true);

        when(identityValidator.isValid(createCustomerDTO.getIdentityProofType(), createCustomerDTO.getIdentityProofId())).thenReturn(true);

        when(customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(
                createCustomerDTO.getEmail(),
                createCustomerDTO.getPhoneNumber(),
                createCustomerDTO.getIdentityProofType(),
                createCustomerDTO.getIdentityProofId()))
                .thenReturn(true);


        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(createCustomerDTO));

        assertEquals("Customer already exists with the same email, phone number and identity proof type", ex.getMessage());
    }


    @Test
    void createCustomer_ThrowsException_InvalidEmail() {
        CreateCustomerDTO createCustomerDTO = dto;
        createCustomerDTO.setEmail("invalid-email");

    }

    @Test
    void createCustomer_ThrowsException_InvalidDateOfBirth() {
        CreateCustomerDTO createCustomerDTO = dto;
        createCustomerDTO.setDateOfBirth(LocalDate.now().plusDays(1));
        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(createCustomerDTO));

        assertEquals("Date of Birth cannot be in future", ex.getMessage());
    }


    @Test
    void createCustomer_ThrowsException_identityValidator(){
        CreateCustomerDTO createCustomerDTO = dto;
        createCustomerDTO.setIdentityProofId("invalid-id");

        when(identityValidator.nameValidate(anyString())).thenReturn(true);

        when(identityValidator.isValid(createCustomerDTO.getIdentityProofType(), createCustomerDTO.getIdentityProofId())).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(createCustomerDTO));

        assertEquals("Invalid identity proof type or id", ex.getMessage());
    }

    @Test
    void createCustomer_ThrowsException_InvalidName() {
        CreateCustomerDTO createCustomerDTO = dto;
        createCustomerDTO.setFirstName("2230~~~");

        when(identityValidator.nameValidate(anyString())).thenReturn(false);
        RuntimeException ex = assertThrows(RuntimeException.class, () -> customerService.createCustomer(createCustomerDTO));

        assertEquals("Invalid name format", ex.getMessage());

    }

}
