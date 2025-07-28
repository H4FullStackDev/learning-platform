package com.example.requisitionmanagementapi.controller;

import com.example.requisitionmanagementapi.dto.*;
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
    public UserDTO create(@RequestBody UserDTO dto) {
        return userService.createUserByAdmin(dto);
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
            @RequestBody Long role
    ) {
        userService.assignRoles(userId, role);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/first-connection")
    public void resetPasswordAtFirstConnection(@RequestBody ResetPasswordRequest request, Principal principal) {
        userService.updatePassword(principal, request.getNewPassword());
    }

    @PutMapping("/{id}/password")
    public void updateAdminPassword(@PathVariable Long id, @RequestBody FirstPasswordReset request) {
        userService.updateAdminPassword(id, request.getNewPassword());
    }

}
