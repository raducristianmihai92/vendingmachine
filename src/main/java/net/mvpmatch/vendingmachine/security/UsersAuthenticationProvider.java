package net.mvpmatch.vendingmachine.security;

import net.mvpmatch.vendingmachine.data.entity.User;
import net.mvpmatch.vendingmachine.exceptions.UserAuthenticationException;
import net.mvpmatch.vendingmachine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsersAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        List<GrantedAuthority> grantedAuths = authenticatedWithUserService(name, password);

        return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
    }

    private List<GrantedAuthority> authenticatedWithUserService(final String name, final String password) {
        List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();

        Optional<User> user = userService.findByUserName(name);

        if(user.isPresent()){
            validatePassword(password, user.get());
            grantedAuths.add(new UserGrantedAuthority(user.get().getRole()));
            return grantedAuths;
        }

        throw new UserAuthenticationException("Unable to authorize against the system. Invalid username or password.");
    }

    private void validatePassword(final String password, User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        if(!encoder.matches(password, user.getPassword())){
            throw new UserAuthenticationException("Unable to authorize against the system. Invalid username or password.");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
