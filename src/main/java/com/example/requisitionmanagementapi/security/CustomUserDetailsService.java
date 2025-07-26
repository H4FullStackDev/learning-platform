package com.example.requisitionmanagementapi.security;

import com.example.requisitionmanagementapi.dao.UserDAO;
import com.example.requisitionmanagementapi.entity.User;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return new SecurityUserPrincipal(user);
    }
}
