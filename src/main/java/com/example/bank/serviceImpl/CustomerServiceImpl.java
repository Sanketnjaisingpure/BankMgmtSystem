package com.example.bank.serviceImpl;


import com.example.bank.Enum.AccountStatus;
import com.example.bank.Enum.CardStatus;
import com.example.bank.Enum.LoanStatus;
import com.example.bank.dto.*;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.Account;
import com.example.bank.model.Address;
import com.example.bank.model.Customer;
import com.example.bank.model.Users;
import com.example.bank.repository.*;
import com.example.bank.service.AccountService;
import com.example.bank.service.CustomerService;
import com.example.bank.utils.IdentityValidator;
import com.example.bank.utils.MaskedNumber;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountService accountService;
    private final CardRepository cardRepository;
    private final ModelMapper modelMapper;
    private final UserDetailsRepo userDetailsRepo;
    private final IdentityValidator identityValidator;
    private final MaskedNumber maskedNumber;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);


    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository,@Lazy AccountService accountService,
            @Lazy CardRepository cardRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder,
            UserDetailsRepo userDetailsRepo,
            MaskedNumber maskedNumber,
            IdentityValidator identityValidator
            ,ModelMapper modelMapper) {
        this.userDetailsRepo = userDetailsRepo;
        this.customerRepository = customerRepository;
        this.maskedNumber = maskedNumber;
        this.modelMapper = modelMapper;
        this.cardRepository = cardRepository;
        this.identityValidator = identityValidator;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.accountService = accountService;
    }


    @Transactional
    @Override
    public CustomerDTO createCustomer(@Valid CreateCustomerDTO createCustomerDTO) {
            log.info("Initiating  Creating Customer");
            // date of birth cannot be in future
            if(createCustomerDTO.getDateOfBirth().isAfter(ChronoLocalDate.from(LocalDateTime.now()))) {
                log.warn("Date of Birth cannot be in future");
                throw new RuntimeException("Date of Birth cannot be in future");
            }

            if(!identityValidator.nameValidate(createCustomerDTO.getFirstName()) ||
                    !identityValidator.nameValidate(createCustomerDTO.getMiddleName()) ||
                    !identityValidator.nameValidate(createCustomerDTO.getLastName())) {

                log.warn("Invalid name format");
                throw new RuntimeException("Invalid name format");
            }

            // validate identity proof type and id
            boolean isValidIdentityProof = identityValidator.isValid(createCustomerDTO.getIdentityProofType(), createCustomerDTO.getIdentityProofId());

            if (!isValidIdentityProof) {
                log.warn("Invalid identity proof type or id");
                throw new IllegalArgumentException("Invalid identity proof type or id");
            }

            if(customerRepository.existsByEmailAndPhoneNumberAndIdentityProofTypeAndIdentityProofId(createCustomerDTO.getEmail(), createCustomerDTO.getPhoneNumber(),
                    createCustomerDTO.getIdentityProofType() , createCustomerDTO.getIdentityProofId())) {
                log.warn("Customer already exists ");
                throw new RuntimeException("Customer already exists with the same email, phone number and identity proof type");
            }


            Customer customer = new Customer();
            customer.setFirstName(createCustomerDTO.getFirstName());
            customer.setMiddleName(createCustomerDTO.getMiddleName());
            customer.setLastName(createCustomerDTO.getLastName());
            customer.setEmail(createCustomerDTO.getEmail());
            customer.setIdentityProofId(createCustomerDTO.getIdentityProofId());
            customer.setIdentityProofType(createCustomerDTO.getIdentityProofType());
            customer.setPhoneNumber(createCustomerDTO.getPhoneNumber());
            customer.setAddress(modelMapper.map(createCustomerDTO.getAddressDTO(),Address.class));
            customer.setDateOfBirth(createCustomerDTO.getDateOfBirth());
            customer.setPassword(createCustomerDTO.getPassword());
            if (customer.getAddress() != null) {
                customer.getAddress().setCustomer(customer);
            }
            customer.setCreatedAt(LocalDateTime.now());


            Customer savedCustomer =   customerRepository.save(customer);

            log.info("customer id {}", savedCustomer.getCustomerId());

            if (userDetailsRepo.findByUsername(createCustomerDTO.getEmail()) != null) {
                log.warn("User with email {} already exists", createCustomerDTO.getEmail());
                throw new RuntimeException("User with this email already exists");
            }

            Users  users = new Users();

            users.setUsername(createCustomerDTO.getEmail());
            users.setPassword(bCryptPasswordEncoder.encode(createCustomerDTO.getPassword()));
            users.setRole("ROLE_CUSTOMER");
            users.setLinkedEntityId(savedCustomer.getCustomerId().toString());
            users.setEntityType("CUSTOMER");

            userDetailsRepo.save(users);
            log.info("Customer {} is created successfully ",
                    maskedNumber.maskNumber(savedCustomer.getCustomerId().toString()));
            return modelMapper.map(createCustomerDTO, CustomerDTO.class);

    }

    @Override
    public CustomerDTO getCustomerDTOById(UUID customerId) {
         Customer customer =  this.findCustomerById(customerId);
         AddressDTO addressDTO = modelMapper.map(customer.getAddress(),AddressDTO.class);

         log.info("Customer found  successfully , mapping to CustomerDTO");
         CustomerDTO customerDTO = modelMapper.map(customer,CustomerDTO.class);
         customerDTO.setAddressDTO(addressDTO);
         return customerDTO;
    }

    @Override
    public Customer findCustomerById(UUID customerId){
        log.info("Initiating find customer by Id {} ", maskedNumber.maskNumber(customerId.toString()));
        Customer customer = customerRepository.findById(customerId).orElseThrow(()-> {
            log.warn("Customer {} not found ", maskedNumber.maskNumber(customerId.toString()));
            return new ResourceNotFoundException("Customer not found");
        });
        log.info("Customer {} found successfully ", maskedNumber.maskNumber(customer.getCustomerId().toString()));
        return customer;
    }

    @Override
    public List<CustomerDTO> getAllCustomer() {
        log.info("Initiating Get All Customers");
        List<Customer> customerList = customerRepository.findAll();

        if (customerList.isEmpty()){
            log.warn("Customers are not yet added , please add one or more ");
            throw new ResourceNotFoundException("Customers not found");
        }
        List<CustomerDTO> customerDTOS = customerList.stream().map(customer -> {
            CustomerDTO customerDTO = modelMapper.map(customer,CustomerDTO.class);
            if (customer.getAddress() != null) {
                AddressDTO addressDTO = modelMapper.map(customer.getAddress(),AddressDTO.class);
                customerDTO.setAddressDTO(addressDTO);
            }
            return customerDTO;
        }).collect(Collectors.toList());
        log.info("Customer's found Successfully ");
        return  customerDTOS;
    }
    @Override
    public CustomerDTO updateCustomerDetails(UUID customerId, UpdateCustomerDTO updateCustomerDTO) {

        log.info("Initiating Updated Customer Details by Id {} ", maskedNumber.maskNumber(customerId.toString()));

        Customer customer = this.findCustomerById(customerId);

        customer.setEmail(updateCustomerDTO.getEmail());
        customer.setIdentityProofId(updateCustomerDTO.getIdentityProofId());


        Address address = customer.getAddress();
        address.setCity(updateCustomerDTO.getAddressDTO().getCity());
        address.setState(updateCustomerDTO.getAddressDTO().getState());
        address.setCountry(updateCustomerDTO.getAddressDTO().getCountry());
        address.setZipCode(updateCustomerDTO.getAddressDTO().getZipCode());
        address.setStreet(updateCustomerDTO.getAddressDTO().getStreet());
        customer.setAddress(address);
        customer.setPhoneNumber(updateCustomerDTO.getPhoneNumber());
        customer.setIdentityProofType(updateCustomerDTO.getIdentityProofType());
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
        log.info("Customer updated successfully id : {} ",maskedNumber.maskNumber(customerId.toString()));
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Transactional
    @Override
    public void deleteCustomer(UUID customerId) {
        log.info("Initiating delete customer by Id {}" , maskedNumber.maskNumber(customerId.toString()));
        Customer customer = this.findCustomerById(customerId);

        // here  need to ensure that customer has no account associated with it,
        // inactive all credit cards and accounts associated with this customer
        // A customer should not have any  active loan associated with it

        log.info("Checking if customer has any active loan associated with it");
        customer.getLoanList().forEach(loan -> {
            if (loan.getLoanStatus().equals(LoanStatus.ACTIVE)) {
                log.warn("Customer has active loan associated with it");
                throw new IllegalArgumentException("Customer has active loan associated with it");
            }
        });
        log.info("Inactive all credit cards ");
        customer.getCardList().forEach(card -> {
            card.setCardStatus(CardStatus.INACTIVE);
            cardRepository.save(card);
        });

        if(!customer.getAccountList().isEmpty()) {
            customer.getAccountList().forEach(account -> account.setAccountStatus(AccountStatus.INACTIVE));
        }

        customerRepository.delete(customer);
        log.info("Customer {} deleted successfully ", maskedNumber.maskNumber(customerId.toString()));
    }

    @Override
    public List<AccountDTO> getCustomerAccounts(UUID customerId) {
        log.info("Initiating Get All Customer's Account's by Customer Id {} ", maskedNumber.maskNumber(customerId.toString()));
        this.findCustomerById(customerId);
        List<Account> accountList = accountService.FindAllCustomerAccountByCustomerId(customerId);

        log.info("Found all Customer's Account list");
        return accountList.stream().map(account -> {
            AccountDTO accountDTO = modelMapper.map(account,AccountDTO.class);
            accountDTO.setFirstName(account.getCustomer().getFirstName());
            accountDTO.setLastName(account.getCustomer().getLastName());
            accountDTO.setMiddleName(account.getCustomer().getMiddleName());
            accountDTO.setBranchName(account.getBranch().getBranchName());
            accountDTO.setBankName(account.getBranch().getBank().getBankName());
            return accountDTO;
        }).collect(Collectors.toList());
    }
}
