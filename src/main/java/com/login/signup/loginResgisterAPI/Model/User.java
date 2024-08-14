package com.login.signup.loginResgisterAPI.Model;


import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


@Entity
@Table(name = "users")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotNull
    @Size(min = 4, max = 20)
    private String username;

    @Column(nullable = false)
    @NotNull
    @Size(min = 6)
    private String password;

    @Column(nullable = false, unique = true)
    @Email
    private String email;
}


