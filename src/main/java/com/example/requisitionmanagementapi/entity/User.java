package com.example.requisitionmanagementapi.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@EntityListeners(AuditingEntityListener.class)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    private boolean enabled;
    private boolean mustChangePassword;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;


    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;

    @ManyToMany
    @JoinTable(
            name = "user_department",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "department_id")
    )
    private Set<Department> departments = new HashSet<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<RefreshToken> refreshTokens;
}
