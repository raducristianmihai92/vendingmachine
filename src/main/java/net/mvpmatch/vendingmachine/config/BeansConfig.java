package net.mvpmatch.vendingmachine.config;

import net.mvpmatch.vendingmachine.security.UsersAuthenticationProvider;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

@Configuration
public class BeansConfig {

    @Bean()
    @Scope("prototype")
    public UsersAuthenticationProvider buildUsersAuthenticationProvider() {
        return new UsersAuthenticationProvider();
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
