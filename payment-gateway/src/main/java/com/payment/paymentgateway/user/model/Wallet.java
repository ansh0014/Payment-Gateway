package com.payment.paymentgateway.user.model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "wallets")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique =true)
    private User user;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal reservedBalance=BigDecimal.ZERO;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt=LocalDateTime.now();
   
    private LocalDateTime lastUpdated=LocalDateTime.now();
    
}
