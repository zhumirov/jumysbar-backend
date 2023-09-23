package kz.btsd.edmarket.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Entry points
        http.cors().and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()//
                .antMatchers(
                        "/actuator/health/liveness",
                        "/actuator/health/readiness",
                        "/auth/**",
                        "/events/**",
                        "/files/**",
                        "/users/reg/**",
                        "/users/token/**",
                        "/users/reset/**",
                        "/subscriptions/reg/**",
                        "/search/**",
                        "/subscriptions/result",
                        "/mobile/dashboard",
                        "/subsection-views/action/**"
                ).permitAll()
                .antMatchers(HttpMethod.OPTIONS).permitAll() //todo crossorigin solution
                .antMatchers(HttpMethod.GET).permitAll() // открытая часть
//                .antMatchers(HttpMethod.DELETE).permitAll() // открытая часть
//                .antMatchers(HttpMethod.POST).permitAll() // открытая часть
//                .antMatchers(HttpMethod.PUT).permitAll() // открытая часть
                .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }

    //todo добавить зависимость от профиля
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs",
                "/configuration/ui",
                "/swagger-resources/**",
                "/configuration/security",
                "/swagger-ui.html",
                "/webjars/**");
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

