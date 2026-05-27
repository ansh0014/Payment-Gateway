package com.payment.paymentgateway.user;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@Table(name = "users")

public class User {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String name;
@Column(unique = true)
private String email;
    
}
