package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.CreateUserRequest;
import com.example.requisitionmanagementapi.dto.ResetPasswordRequest;
import com.example.requisitionmanagementapi.dto.UpdateUserRequest;
import com.example.requisitionmanagementapi.dto.UserDTO;
import com.example.requisitionmanagementapi.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable Long id) {
        return userService.getById(id);
    }

    @PostMapping
    public UserDTO create(@RequestBody CreateUserRequest request) {
        return userService.createUserByAdmin(request);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return userService.updateUserByAdmin(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasRole('SUPERADMIN')")
    public ResponseEntity<?> assignRoles(
            @PathVariable Long userId,
            @RequestBody Set<Long> roleIds
    ) {
        userService.assignRoles(userId, roleIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/first-connection")
    public ResponseEntity<?> resetPasswordAtFirstConnection(@RequestBody ResetPasswordRequest request, Principal principal) {
        // Ici tu peux ajouter des vérifications de robustesse sur le nouveau mot de passe
        userService.updatePassword(principal, request.getNewPassword());
        return ResponseEntity.ok("Mot de passe changé avec succès.");
    }

}
