package com.example.requisitionmanagementapi.Runner;

import com.example.requisitionmanagementapi.dao.RoleDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.entity.Role;
import com.example.requisitionmanagementapi.entity.User;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SuperAdminInitializer implements CommandLineRunner {

    private final UserDAO userDao;
    private final RoleDAO roleDao;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Vérifie si un superadmin existe déjà
        boolean exists = userDao.findAll().stream()
                .anyMatch(user -> user.getRoles().stream().anyMatch(role -> role.getName().equals("SUPERADMIN")));
        if (!exists) {
            Role superAdminRole = roleDao.findByName("SUPERADMIN")
                    .orElseGet(() -> roleDao.save(Role.builder().name("SUPERADMIN").build()));

            String tempPassword = "admin";
            User superAdmin = User.builder()
                    .email("superadmin@monsite.tg")
                    .username("superadmin")
                    .password(passwordEncoder.encode(tempPassword))
                    .enabled(true)
                    .mustChangePassword(true)
                    .roles(Set.of(superAdminRole))
                    .build();
            userDao.save(superAdmin);

            System.out.println("----------- SUPERADMIN CREATED -----------");
            System.out.println("Email: superadmin@monsite.tg");
            System.out.println("Mot de passe initial: " + tempPassword);
            System.out.println("Changez ce mot de passe à la première connexion !");
            System.out.println("------------------------------------------");
        }
    }
}
