package com.prajekpro.api.dto;

import com.safalyatech.common.domains.RoleMasterDtl;
import com.safalyatech.common.domains.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
public class UserDetailsProxy implements UserDetails {

    private String proxyUsername;

    private String proxyPassword;

    private Set<RoleMasterDtl> roles;

    public UserDetailsProxy(Users userDetails) {
        this.proxyUsername = userDetails.getUsername();
        this.proxyPassword = userDetails.getPassword();
        this.roles = userDetails.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>();

        for (RoleMasterDtl role : roles) {
            grantedAuthorities.add(new SimpleGrantedAuthority(role.getRoles()));
        }

        return grantedAuthorities;
    }

    @Override
    public String getPassword() {
        return this.proxyPassword;
    }

    @Override
    public String getUsername() {
        return this.proxyUsername;
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
}
