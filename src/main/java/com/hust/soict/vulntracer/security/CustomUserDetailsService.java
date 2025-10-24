package com.hust.soict.vulntracer.security;

import com.hust.soict.vulntracer.model.User;
import com.hust.soict.vulntracer.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("Not found user: " + username);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getHashedPassword(),
                Collections.singleton(() -> user.getRole().toString())
        );
    }
}
