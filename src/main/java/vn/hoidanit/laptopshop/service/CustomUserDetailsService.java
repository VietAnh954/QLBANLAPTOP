package vn.hoidanit.laptopshop.service;

import java.util.Collections;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import vn.hoidanit.laptopshop.domain.User;
import vn.hoidanit.laptopshop.repository.UserRepository;

public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<User> user = this.userRepository.findByEmail(username);

        if (user.isPresent()) {
            String email = user.get().getEmail();
            String password = user.get().getPassword();

            return new org.springframework.security.core.userdetails.User(
                    email,
                    password,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" +
                            user.get().getRole().getName())));

        } else {
            throw new UsernameNotFoundException("User not found with username: " +
                    username);
        }

    }

}
