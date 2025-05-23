package vn.hoidanit.laptopshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.session.security.web.authentication.SpringSessionRememberMeServices;

import jakarta.servlet.DispatcherType;
import vn.hoidanit.laptopshop.repository.UserRepository;
import vn.hoidanit.laptopshop.service.CustomUserDetailsService;
import vn.hoidanit.laptopshop.service.UserService;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService) {
        return new CustomUserDetailsService(userService);
    }

    @Bean
    public AuthenticationSuccessHandler customSuccessHandler() {
        return new CustomSuccessHandler();
    }

    @Bean
    public DaoAuthenticationProvider authProvider(
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailsService) {

        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        // authProvider.setHideUserNotFoundExceptions(false);

        return authProvider;
    }

    @Bean
    public SpringSessionRememberMeServices rememberMeServices() {
        SpringSessionRememberMeServices rememberMeServices = new SpringSessionRememberMeServices();
        // optionally customize
        rememberMeServices.setAlwaysRemember(true);

        return rememberMeServices;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD,
                                DispatcherType.INCLUDE)
                        .permitAll()

                        .requestMatchers("/",
                                "/login",
                                "/product/**",
                                "/client/**",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/register")

                        .permitAll()

                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().authenticated())
                .sessionManagement((sessionManagement) -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/logout?expired")
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false))

                .logout(logout -> logout.deleteCookies("JSESSIONID").invalidateHttpSession(true))

                .rememberMe(r -> r.rememberMeServices(rememberMeServices()))
                .formLogin(formLogin -> formLogin
                        .loginPage("/login")
                        .failureUrl("/login?error")
                        .successHandler(customSuccessHandler())
                        .permitAll())
                .exceptionHandling(ex -> ex.accessDeniedPage("/accessDeny"));

        return http.build();
    }

    // @Bean
    // public UserDetailsService userDetailsService(UserRepository userRepository) {
    // return new CustomUserDetailsService(userRepository);
    // }

    // @Bean
    // public AuthenticationSuccessHandler customSuccessHandler() {
    // return new CustomSuccessHandler();
    // }

    // @Bean
    // public DaoAuthenticationProvider authProvider(
    // PasswordEncoder passwordEncoder,
    // UserDetailsService userDetailsService) {
    // DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    // authProvider.setUserDetailsService(userDetailsService);
    // authProvider.setPasswordEncoder(passwordEncoder);
    // return authProvider;
    // }

    // // @Bean
    // // public SpringSessionRememberMeServices rememberMeServices() {
    // // SpringSessionRememberMeServices rememberMeServices = new
    // // SpringSessionRememberMeServices();
    // // // optionally customize
    // // rememberMeServices.setAlwaysRemember(true);
    // // return rememberMeServices;
    // // }

    // @Bean
    // SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // http
    // .authorizeHttpRequests(authorize -> authorize
    // .dispatcherTypeMatchers(DispatcherType.FORWARD,
    // DispatcherType.INCLUDE)
    // .permitAll()
    // .requestMatchers("/", "/login", "/register", "/client/**", "/css/**",
    // "/js/**",
    // "/images/**")
    // .permitAll()

    // .requestMatchers("/admin/**").hasRole("ADMIN")

    // .anyRequest().permitAll())
    // // khi logout thì xóa luôn cookie
    // // .sessionManagement((sessionManagement) -> sessionManagement
    // // .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
    // // .invalidSessionUrl("/logout?expired")
    // // .maximumSessions(1)
    // // .maxSessionsPreventsLogin(false))
    // // .logout(logout ->
    // // logout.deleteCookies("JSESSIONID").invalidateHttpSession(true))

    // // cơ chế remember me

    // .formLogin(formLogin -> formLogin
    // .loginPage("/login")
    // .failureUrl("/login?error")

    // .successHandler(customSuccessHandler())
    // .permitAll())
    // // nếu mà Role_User mà vào trang admin thì nó đẩy ra trang này /accessDeny
    // .exceptionHandling(ex -> ex.accessDeniedPage("/accessDeny"));
    // return http.build();
    // }
}
