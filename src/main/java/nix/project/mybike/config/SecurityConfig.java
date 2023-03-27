package nix.project.mybike.config;


import nix.project.mybike.services.ClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;


@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ClientDetailsService clientDetailsService;

    @Autowired
    public SecurityConfig(ClientDetailsService clientDetailsService) {
        this.clientDetailsService = clientDetailsService;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(clientDetailsService).passwordEncoder(getPasswordEncoder());
    }

    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();

    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity.
                 authorizeHttpRequests()
                .antMatchers("/clients").hasRole("ADMIN")
                .antMatchers("/debts").hasRole("ADMIN")
                .antMatchers("/auth/login", "/error", "/auth/registration").permitAll()
                .anyRequest().hasAnyRole("USER", "ADMIN")
                .and()
                .formLogin().loginPage("/auth/login").loginProcessingUrl("/process_login")
                .defaultSuccessUrl("/bikes", true)
                .failureUrl("/auth/login?error")
                .and()
                .logout().logoutUrl("/logout").logoutSuccessUrl("/auth/login");

    }


}
