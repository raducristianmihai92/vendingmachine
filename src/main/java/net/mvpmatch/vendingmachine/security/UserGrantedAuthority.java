package net.mvpmatch.vendingmachine.security;

import org.springframework.security.core.GrantedAuthority;

public class UserGrantedAuthority implements GrantedAuthority {

    private String authority = "";

    public UserGrantedAuthority(final String authority){
        this.authority = authority;
    }

    @Override
    public String getAuthority() {
        return authority;
    }
}
