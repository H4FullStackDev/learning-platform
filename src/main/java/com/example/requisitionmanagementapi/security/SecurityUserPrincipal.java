package com.example.requisitionmanagementapi.security;

import com.example.requisitionmanagementapi.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class SecurityUserPrincipal implements UserDetails {

    // accès direct si besoin
    private final User user;

    public SecurityUserPrincipal(User user) {
        this.user = user;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Si l'utilisateur a un rôle, retourne les permissions de ce rôle comme autorités
        if (user.getRole() != null) {
            return user.getRole().getPermissions().stream()
                    .map(permission -> (GrantedAuthority) () -> permission.getName())
                    .collect(Collectors.toSet());
        }
        // Sinon, retourne une collection vide
        return Collections.emptySet();
    }

    public String getEmail() { return user.getEmail(); }

    public String getRole() { return user.getRole().getName(); }

    @Override
    public String getPassword() { return user.getPassword(); }

    @Override
    public String getUsername() { return user.getUsername(); } // login by username

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return user.isEnabled(); }

}

