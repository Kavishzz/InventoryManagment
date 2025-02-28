package com.inventorySystemDev.InventoryMgtSystem.models;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.inventorySystemDev.InventoryMgtSystem.enums.TransactionStatus;
import com.inventorySystemDev.InventoryMgtSystem.enums.TransactionType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transactions")
@Data
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private Integer totalProducts;

    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private TransactionType transactionType; // purchase, sale, return
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status; //pending, completed, processing

    private String description;
    private String note;

    private final LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Override
    public String toString() {
        return "Transaction [id=" + id + ", totalProducts=" + totalProducts + ", totalPrice=" + totalPrice
                + ", transactionType=" + transactionType + ", status=" + status + ", description=" + description
                + ", note=" + note + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + "]";
    }

    

}
