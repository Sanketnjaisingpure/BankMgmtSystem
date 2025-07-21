package com.example.bank.serviceImpl;

import com.example.bank.dto.*;
import com.example.bank.exception.ResourceNotFoundException;
import com.example.bank.model.*;
import com.example.bank.repository.BankRepository;
import com.example.bank.repository.BranchRepository;
import com.example.bank.service.BranchService;
import com.example.bank.utils.IFSCUtil;
import com.example.bank.utils.MaskedNumber;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;
    private final ModelMapper modelMapper;
    private final BankRepository bankRepository;
    private final MaskedNumber maskedNumber;

    private static final Logger log = LoggerFactory.getLogger(BranchServiceImpl.class);

    @Autowired
    public BranchServiceImpl(BranchRepository branchRepository, ModelMapper modelMapper, BankRepository bankRepository,
                           MaskedNumber maskedNumber) {
        this.branchRepository = branchRepository;
        this.maskedNumber = maskedNumber;
        this.modelMapper = modelMapper;
        this.bankRepository = bankRepository;
    }

    @Override
    public  Branch findBranchByBranchId(Long branchId){
        log.info("Initiated Finding Branch by branch Id: {} " , branchId);
        Branch  branch = branchRepository.findById(branchId).orElseThrow(()->new ResourceNotFoundException("Branch Not found"));
        log.info("branch found");
        return branch;
    }

    public List<Customer>  getAllCustomerByBranch(long branchId){
        log.info("Initiated Finding All Customer by branch Id: {} " , branchId);
        Branch branch = findBranchByBranchId(branchId);

        List<Customer> customerList = branch.getAccountList().stream().map(Account::getCustomer).toList();
        if (customerList.isEmpty()){
            log.warn("Customer not found in this branch {}", branch.getBranchName());
            throw new ResourceNotFoundException("Customer not found");
        }
        log.info("Customer found successfully in this branch {}", branch.getBranchName());
        return customerList;
    }
    @Transactional
    @Override
    public BranchDTO createBranch(CreateBranchDTO createBranchDTO) {

        log.info("Creating Branch Initiated");

        if (createBranchDTO==null){
            log.warn("Enter proper values");
            throw new IllegalArgumentException("Enter values properly");
        }

        AddressDTO addressDTO = createBranchDTO.getAddressDTO();

        Address address = modelMapper.map(addressDTO, Address.class);


        Branch branch = new Branch();
        branch.setBranchName(createBranchDTO.getBranchName());
        Bank bank = bankRepository.findById(createBranchDTO.getBankId()).orElseThrow(()->
                new ResourceNotFoundException("Bank not found with Id " +  maskedNumber.maskNumber( createBranchDTO.getBankId().toString())));

        branch.setBank(bank);
        String bankName = bank.getBankName();
        String prefix = IFSCUtil.getBankIfscPrefix(bankName);
        int random_number =  new Random().nextInt(10000); // Generate a random number between 0 and 9999
        String ifscCode = prefix + String.format("%03d", random_number);
        branch.setIfscCode(ifscCode);
        address.setBranch(branch);     // very important for @OneToOne(mappedBy="branch")
        branch.setAddress(address);    // important for cascade to persist address

        Branch savedBranch = branchRepository.save(branch);

        BranchDTO branchDTO = modelMapper.map(savedBranch, BranchDTO.class);

        branchDTO.setBankSummaryDTO(modelMapper.map(bank, BankSummaryDTO.class));
        branchDTO.setAddressDTO(modelMapper.map(address, AddressDTO.class));
        log.info("Branch Created Successfully");
        return branchDTO;
    }


    @Override
    public BranchDTO getBranchDTOById(Long branchId) {
        log.info("Getting Branch by Id: {} ",branchId);
        Branch branch = this.findBranchByBranchId(branchId);
        BranchDTO branchDTO = modelMapper.map(branch,BranchDTO.class);
        AddressDTO addressDTO = modelMapper.map(branch.getAddress(),AddressDTO.class);
        branchDTO.setAddressDTO(addressDTO);
        Bank bank = branch.getBank();
        branchDTO.setBankSummaryDTO(modelMapper.map(bank,BankSummaryDTO.class));
        log.info("Branch found successfully ");
        return branchDTO;
    }

    @Override
    public List<BranchDTO> getAllBranchByBank(UUID bankId) {
        log.info("Getting Branch by Bank Id: {} ",maskedNumber.maskNumber(bankId.toString()));
        Bank bank = bankRepository.findById(bankId).orElseThrow(()-> new ResourceNotFoundException("Bank not found"));
        if (bank==null){
            log.warn("Bank not found with Id: {}", maskedNumber.maskNumber(bankId.toString()));
            throw new ResourceNotFoundException("Bank not found");
        }

        List<Branch> branchList= branchRepository.findByBank_BankId(bankId);
        if (branchList.isEmpty()){
            log.warn("Branch not found");
            throw new ResourceNotFoundException("Branch not found");
        }
        log.info("Branch found successfully");
        return branchList.stream().map(branch -> {
            BranchDTO branchDTO = modelMapper.map(branch,BranchDTO.class);
            AddressDTO addressDTO = modelMapper.map(branch.getAddress(),AddressDTO.class);
            branchDTO.setAddressDTO(addressDTO);
            Bank bank1 = branch.getBank();
            branchDTO.setBankSummaryDTO(modelMapper.map(bank1,BankSummaryDTO.class));
            return branchDTO;
        }).collect(Collectors.toList());
    }

    @Override
    public List<CustomerDTO> getCustomerDTOByBranch(long branchId) {
        log.info("Getting Customer DTO by Branch Id: {} ",branchId);

        List<Customer> customerList = this.getAllCustomerByBranch(branchId);

        return customerList.stream().map(customer -> modelMapper.map(customer,CustomerDTO.class)).collect(Collectors.toList());
    }

}
