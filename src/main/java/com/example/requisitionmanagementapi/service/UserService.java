package com.example.requisitionmanagementapi.service;

import com.example.requisitionmanagementapi.Utils.MailService;
import com.example.requisitionmanagementapi.dao.RoleDAO;
import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.dto.CreateUserRequest;
import com.example.requisitionmanagementapi.dto.UpdateUserRequest;
import com.example.requisitionmanagementapi.dto.UserDTO;
import com.example.requisitionmanagementapi.entity.Role;
import com.example.requisitionmanagementapi.entity.User;
import com.example.requisitionmanagementapi.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserDAO userDao;
    private final RoleDAO roleDao;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    public List<UserDTO> getAll() {
        return userMapper.toDTOList(userDao.findAll());
    }

    public UserDTO getById(Long id) {
        return userDao.findById(id).map(userMapper::toDTO).orElse(null);
    }

    @Transactional
    public UserDTO createUserByAdmin(CreateUserRequest request) {
        String tempPassword = generateTemporaryPassword();

        Set<Role> roles = request.getRoleIds().stream()
                .map(rid -> roleDao.findById(rid).orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());

        User user = User.builder()
                .email(request.getEmail())
                .username(request.getUsername())
                .password(passwordEncoder.encode(tempPassword))
                .enabled(true)
                .mustChangePassword(true)
                .roles(roles)
                .build();

        userDao.save(user);

        mailService.sendNewAccountEmail(user.getEmail(), tempPassword);

        return userMapper.toDTO(user);
    }

    @Transactional
    public UserDTO updateUserByAdmin(Long id, UpdateUserRequest request) {
        User user = userDao.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        userDao.save(user);
        return userMapper.toDTO(user);
    }

    public void delete(Long id) {
        userDao.deleteById(id);
    }

    @Transactional
    public void assignRoles(Long userId, Set<Long> roleIds) {
        User user = userDao.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Set<Role> roles = roleIds.stream()
                .map(rid -> roleDao.findById(rid).orElseThrow(() -> new RuntimeException("Role not found")))
                .collect(Collectors.toSet());
        user.setRoles(roles);
        userDao.save(user);
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 10) + "@aA1";
    }
}


