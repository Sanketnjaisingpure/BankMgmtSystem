package com.example.bank.serviceImpl;

import com.example.bank.dto.*;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.*;
import com.example.bank.repository.BankRepository;
import com.example.bank.repository.BranchRepository;
import com.example.bank.utils.MaskedNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchServiceImplTest {


    @Mock
    private BranchRepository branchRepository;

    @Mock
    private BankRepository bankRepository;

    @Mock
    private MaskedNumber maskedNumber;


    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private BranchServiceImpl branchServiceImpl;

    private Branch testBranch;

    private Bank testBank;

    private Customer testCustomer;

    private Address testAddress;

    private Account testAccount;

    private BranchDTO testBranchDTO;

    private AddressDTO testAddressDTO;


    @BeforeEach
    void setUp() {
        // Initialize any necessary objects or mocks here

        testAddress = new Address();
        testAddress.setAddressId(1L);
        testAddress.setStreet("123 Test St");
        testAddress.setCity("Test City");
        testAddress.setState("Test State");
        testAddress.setZipCode("123454");
        testAddress.setCountry("Test Country");

        testBank = new Bank();
        testBank.setBankId(UUID.randomUUID());
        testBank.setBankName("Test Bank");
        testBank.setHeadOfficeAddress("123 Test St, Test City");


       testBranch = new Branch();
       testBranch.setBranchId(1L);
       testBranch.setBranchName("Test Branch");
       testBranch.setIfscCode("TEST0001");
       testBranch.setBank(testBank);
       testBranch.setAddress(testAddress);

         testCustomer = new Customer();
        testCustomer.setCustomerId(UUID.randomUUID());
        testCustomer.setFirstName("Test Customer");
        testCustomer.setLastName("Last Name");
        testCustomer.setAddress(testAddress);


        testAccount = new Account();
        testAccount.setAccountId(UUID.randomUUID());
        testAccount.setAccountNumber("1234567890");
        testAccount.setBranch(testBranch);
        testCustomer.setAccountList(List.of(testAccount));


        testBranchDTO = new BranchDTO();
        testBranchDTO.setBranchName(testBranch.getBranchName());
        testBranchDTO.setIfscCode(testBranch.getIfscCode());

        testAddressDTO = new AddressDTO();
        testAddressDTO.setStreet(testAddress.getStreet());
        testAddressDTO.setCity(testAddress.getCity());
        testAddressDTO.setState(testAddress.getState());
        testAddressDTO.setZipCode(testAddress.getZipCode());
        testAddressDTO.setCountry(testAddress.getCountry());
        testBranchDTO.setAddressDTO(testAddressDTO);
    }

    @Test
    void findBranchByBranchId_Success() {
        // Test case for finding a branch by its ID
        // Mock the repository call and assert the expected behavior
        Branch branch = testBranch;
        when(branchRepository.findById(branch.getBranchId())).thenReturn(Optional.of(branch));
        Branch foundBranch = branchServiceImpl.findBranchByBranchId(1L);
        assertNotNull(foundBranch);
        assertEquals("Test Branch", foundBranch.getBranchName());
    }

    @Test
    void findBranchByBranchId_NotFound(){

        Branch branch = testBranch;
        when(branchRepository.findById(branch.getBranchId())).thenReturn(Optional.empty());
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,()->  branchServiceImpl.findBranchByBranchId(branch.getBranchId()));

        assertEquals("Branch Not found", ex.getMessage());
    }

    @Test
    void getAllCustomerByBranch_Success(){
        Customer customer1 = testCustomer;
        customer1.setAccountList(List.of(testAccount));
        Customer customer2 = testCustomer;
        customer2.setAccountList(List.of(testAccount));

        Branch branch = testBranch;
        branch.setAccountList(List.of(testAccount, testAccount));

        when(branchRepository.findById(branch.getBranchId())).thenReturn(Optional.of(branch));
        List<Customer> customerList = branch.getAccountList().stream().map(Account::getCustomer).toList();

        assertNotNull(customerList);

        List<Customer> foundCustomers = branchServiceImpl.getAllCustomerByBranch(branch.getBranchId());

        assertNotNull(foundCustomers);

        assertEquals(2, foundCustomers.size());

    }

    @Test
    void getAllCustomerByBranch_NotFound() {
        // Test case for when no customers are found in the branch
        Branch branch = testBranch;
        branch.setAccountList(List.of());

        when(branchRepository.findById(branch.getBranchId())).thenReturn(Optional.of(branch));

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                branchServiceImpl.getAllCustomerByBranch(branch.getBranchId()));

        assertEquals("Customer not found", ex.getMessage());
    }

    @Test
    void getCustomerDTOByBranch(){

        Customer customer1 = testCustomer;
        customer1.setAccountList(List.of(testAccount));
        Customer customer2 = testCustomer;
        customer2.setAccountList(List.of(testAccount));

        Branch branch = testBranch;
        branch.setAccountList(List.of(testAccount, testAccount));

        when(branchRepository.findById(branch.getBranchId())).thenReturn(Optional.of(branch));

        List<CustomerDTO> customerDTOList = branchServiceImpl.getCustomerDTOByBranch(branch.getBranchId());

        assertNotNull(customerDTOList);

        assertEquals(2, customerDTOList.size());
    }

    @Test
    void getAllBranchByBank_Success(){

        Branch branch1 = testBranch;

        Branch branch2 = testBranch;

        Bank bank = testBank;

        bank.setBranch(List.of(branch1, branch2));

        when(maskedNumber.maskNumber(anyString())).thenReturn("******7890");
        when(bankRepository.findById(bank.getBankId())).thenReturn(Optional.of(bank));

        when(branchRepository.findByBank_BankId(bank.getBankId())).thenReturn(List.of(branch1, branch2));

        when(modelMapper.map(testBranch, BranchDTO.class)).thenReturn(testBranchDTO);
        when(modelMapper.map(testAddress, AddressDTO.class)).thenReturn(testAddressDTO);
        when(modelMapper.map(testBank, BankSummaryDTO.class)).thenReturn(new BankSummaryDTO());

        List<BranchDTO> branchDTOList = branchServiceImpl.getAllBranchByBank(bank.getBankId());

        assertNotNull(branchDTOList);
    }

    @Test
    void getAllBranchByBank_BankNotFound(){
        UUID bankId = UUID.randomUUID();
        when(bankRepository.findById(bankId)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                branchServiceImpl.getAllBranchByBank(bankId));

        assertEquals("Bank not found", ex.getMessage());
    }

    @Test
    void getAllBranchByBank_BranchNotFound(){
        UUID bankId = UUID.randomUUID();
        Bank bank = testBank;
        bank.setBranch(List.of());

        when(maskedNumber.maskNumber(anyString())).thenReturn("******7890");
        when(bankRepository.findById(bankId)).thenReturn(Optional.of(bank));
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                branchServiceImpl.getAllBranchByBank(bankId));

        assertEquals("Branch not found", ex.getMessage());
    }

    @Test
    void getBranchDTOById_Success() {
        // Test case for getting a branch by its ID
        Branch branch =testBranch;
        when(branchRepository.findById(branch.getBranchId())).thenReturn(Optional.of(branch));

        when(modelMapper.map(branch, BranchDTO.class)).thenReturn(testBranchDTO);
        when(modelMapper.map(testAddress, AddressDTO.class)).thenReturn(testAddressDTO);
        when(modelMapper.map(testBank, BankSummaryDTO.class)).thenReturn(new BankSummaryDTO());

        BranchDTO foundBranchDTO = branchServiceImpl.getBranchDTOById(branch.getBranchId());
        assertNotNull(foundBranchDTO);
    }

    @Test
    void createBranch_Success(){
        CreateBranchDTO createBranchDTO = new CreateBranchDTO();
        createBranchDTO.setBranchName("New Branch");
        createBranchDTO.setBankId(testBank.getBankId());
        createBranchDTO.setAddressDTO(testAddressDTO);

        when(modelMapper.map(createBranchDTO.getAddressDTO(), Address.class)).thenReturn(testAddress);

        when(bankRepository.findById(testBank.getBankId())).thenReturn(Optional.of(testBank));

//        when(IFSCUtil.getBankIfscPrefix(testBank.getBankName())).thenReturn(testBank.getBankName());

        when(branchRepository.save(any(Branch.class))).thenReturn(testBranch);

        when(modelMapper.map(testBranch, BranchDTO.class)).thenReturn(testBranchDTO);
        when(modelMapper.map(testAddress, AddressDTO.class)).thenReturn(testAddressDTO);
        when(modelMapper.map(testBank, BankSummaryDTO.class)).thenReturn(new BankSummaryDTO());

        BranchDTO createdBranchDTO = branchServiceImpl.createBranch(createBranchDTO);
        assertNotNull(createdBranchDTO);

    }

    @Test
    void createBranch_NullInput() {
        // Test case for creating a branch with null input
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                branchServiceImpl.createBranch(null));

        assertEquals("Enter values properly", ex.getMessage());
    }

    @Test
    void createBranch_BankNotFound() {
        // Setup
        UUID fakeBankId = UUID.randomUUID();

        CreateBranchDTO createBranchDTO = new CreateBranchDTO();
        createBranchDTO.setBranchName("New Branch");
        createBranchDTO.setBankId(fakeBankId);
        createBranchDTO.setAddressDTO(testAddressDTO);

        when(maskedNumber.maskNumber(fakeBankId.toString())).thenReturn("******7890");
        when(bankRepository.findById(fakeBankId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () ->
                branchServiceImpl.createBranch(createBranchDTO));

        assertEquals("Bank not found with Id ******7890", ex.getMessage());
    }

}