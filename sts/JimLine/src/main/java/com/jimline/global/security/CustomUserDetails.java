package com.jimline.global.security;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.jimline.user.domain.User;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

    private final User user; // 우리 프로젝트의 User 엔티티

    public CustomUserDetails(User user) {
        this.user = user;
    }

    // 현재 사용자의 권한을 반환 (예: ROLE_USER)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() { return user.getUserPw(); }

    @Override
    public String getUsername() { return user.getEmail(); } // 또는 userId

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return true; }
}
