package ru.otus.hw.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.domain.User;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class SecurityUserDetails implements UserDetails {

    private final User user;

    public SecurityUserDetails(User user) {
        this.user = user;
    }

    @Transactional(readOnly = true)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles(user);
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public List<String> getRoles() {
        return user.getRoles().stream()
                .map(Enum::name)
                .toList();
    }

    private List<SimpleGrantedAuthority> getRoles(User user) {
        return user.getRoles().stream()
                .map(role -> "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }
}
