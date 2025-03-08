package com.inventorySystemDev.InventoryMgtSystem.services.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.inventorySystemDev.InventoryMgtSystem.dtos.Response;
import com.inventorySystemDev.InventoryMgtSystem.dtos.TransactionDTO;
import com.inventorySystemDev.InventoryMgtSystem.dtos.TransactionRequest;
import com.inventorySystemDev.InventoryMgtSystem.enums.TransactionStatus;
import com.inventorySystemDev.InventoryMgtSystem.enums.TransactionType;
import com.inventorySystemDev.InventoryMgtSystem.exceptions.NameValueRequiredException;
import com.inventorySystemDev.InventoryMgtSystem.exceptions.NotFoundException;
import com.inventorySystemDev.InventoryMgtSystem.models.Product;
import com.inventorySystemDev.InventoryMgtSystem.models.Supplier;
import com.inventorySystemDev.InventoryMgtSystem.models.Transaction;
import com.inventorySystemDev.InventoryMgtSystem.models.User;
import com.inventorySystemDev.InventoryMgtSystem.repositories.ProductRepository;
import com.inventorySystemDev.InventoryMgtSystem.repositories.SupplierRepository;
import com.inventorySystemDev.InventoryMgtSystem.repositories.TransactionRepository;
import com.inventorySystemDev.InventoryMgtSystem.services.TransactionService;
import com.inventorySystemDev.InventoryMgtSystem.services.UserService;
import com.inventorySystemDev.InventoryMgtSystem.specification.TransactionFilter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService{
    
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;
    private final SupplierRepository supplierRepository;
    private final UserService UserService;
    private final ModelMapper modelMapper;
    
    @Override
    public Response purchase(TransactionRequest transactionRequest) {
        
        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        if(supplierId == null) throw new NameValueRequiredException("Supplier Is is Required");

        Product product = productRepository.findById(productId).orElseThrow(
                            () -> new NotFoundException("Product Not Found"));

        Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(
                            () -> new NotFoundException("Supplier Not Found"));

        User user = UserService.getCurrentLoggedInUser();

        //update the stock quantity and re-save
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);

        //create a transaction
        Transaction transaction = Transaction.builder()
                            .transactionType(TransactionType.PURCHASE)
                            .status(TransactionStatus.COMPLETED)
                            .product(product)
                            .user(user)
                            .supplier(supplier)
                            .totalProducts(quantity)
                            .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                            .note(transactionRequest.getNote())
                            .build();

        transactionRepository.save(transaction);

        return Response.builder()
                    .status(200)
                    .message("Purchase Made Successfully")
                    .build();
        
    }

    @Override
    public Response sell(TransactionRequest transactionRequest) {
        
        Long productId = transactionRequest.getProductId();
        Integer quantity = transactionRequest.getQuantity();
        
        Product product = productRepository.findById(productId).orElseThrow(
                            () -> new NotFoundException("Product Not Found"));

        User user = UserService.getCurrentLoggedInUser();

        //update the stock quantity and re-save
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        //create a transaction
        Transaction transaction = Transaction.builder()
                            .transactionType(TransactionType.SALE)
                            .status(TransactionStatus.COMPLETED)
                            .product(product)
                            .user(user)
                            .totalProducts(quantity)
                            .totalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)))
                            .description(transactionRequest.getDescription())
                            .note(transactionRequest.getNote())
                            .build();

        transactionRepository.save(transaction);

        return Response.builder()
                    .status(200)
                    .message("Product Sale Successfully Made")
                    .build();
        
    }

    @Override
    public Response returnToSupplier(TransactionRequest transactionRequest) {
        
        
        Long productId = transactionRequest.getProductId();
        Long supplierId = transactionRequest.getSupplierId();
        Integer quantity = transactionRequest.getQuantity();

        if(supplierId == null) throw new NameValueRequiredException("Supplier Is is Required");

        Product product = productRepository.findById(productId).orElseThrow(
                            () -> new NotFoundException("Product Not Found"));

        // Supplier supplier = supplierRepository.findById(supplierId).orElseThrow(
        //                     () -> new NotFoundException("Supplier Not Found"));

        User user = UserService.getCurrentLoggedInUser();

        //update the stock quantity and re-save
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);


        //create a transaction
        Transaction transaction = Transaction.builder()
                            .transactionType(TransactionType.RETURN_TO_SUPPLIER)
                            .status(TransactionStatus.PROCESSING)
                            .product(product)
                            .user(user)
                            .totalProducts(quantity)
                            .totalPrice(BigDecimal.ZERO)
                            .description(transactionRequest.getDescription())
                            .note(transactionRequest.getNote())
                            .build();

        transactionRepository.save(transaction);

        return Response.builder()
                    .status(200)
                    .message("Product Return In Progress")
                    .build();
    }

    @Override
    public Response getAllTransactions(int page, int size, String filter) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        //use the transaction specification
        Specification<Transaction> spec = TransactionFilter.byFilter(filter);
        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<TransactionDTO> transactionDTOS = modelMapper.map(transactionPage.getContent(), new TypeToken<List<TransactionDTO>>()
                                    {}.getType());
        
        transactionDTOS.forEach(transactionDTO -> {
            transactionDTO.setUser(null);
            transactionDTO.setProduct(null);
            transactionDTO.setSupplier(null);
        });

        return Response.builder()
                    .status(200)
                    .message("success")
                    .transactions(transactionDTOS)
                    .totalElements(transactionPage.getTotalElements())
                    .totalPages(transactionPage.getTotalPages())
                    .build();
    }

    @Override
    public Response getAllTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                    .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        TransactionDTO transactionDTO = modelMapper.map(transaction, TransactionDTO.class);

        transactionDTO.setUser(null);

        return Response.builder()
                    .status(200)
                    .message("success")
                    .transaction(transactionDTO)
                    .build();
    }

    @Override
    public Response getAllTransactionByMonthAndYear(int month, int year) {
        List<Transaction> transactions = transactionRepository.findAll(TransactionFilter.byMonthAndYear(month, year));

        List<TransactionDTO> transactionDTOs = modelMapper.map(transactions, new TypeToken<List<TransactionDTO>> ()
                                        {}.getType());

        transactionDTOs.forEach(transactionDTO -> {
            transactionDTO.setUser(null);
            transactionDTO.setProduct(null);
            transactionDTO.setSupplier(null);
        });

        return Response.builder()
                    .status(200)
                    .message("success")
                    .transactions(transactionDTOs)
                    .build();
    }

    @Override
    public Response updateTransactionStatus(Long transactionId, TransactionStatus status) {
        
        Transaction existingTransaction = transactionRepository.findById(transactionId)
                    .orElseThrow(() -> new NotFoundException("Transaction Not Found"));

        existingTransaction.setStatus(status);
        existingTransaction.setUpdatedAt(LocalDateTime.now());

        transactionRepository.save(existingTransaction);

        return Response.builder()
                    .status(200)
                    .message("Transaction Status Successfully Updated")
                    .build();
    }

}
