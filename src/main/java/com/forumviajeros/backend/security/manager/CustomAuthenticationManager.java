package com.forumviajeros.backend.security.manager;

import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.forumviajeros.backend.model.User;
import com.forumviajeros.backend.service.user.UserService;

@Component
public class CustomAuthenticationManager implements AuthenticationManager {

    private final UserService userService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomAuthenticationManager(UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        User user = userService.findByUsername(username);

        if (user == null) {
            throw new BadCredentialsException("Usuario no encontrado");
        }

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Contraseña incorrecta");
        }

        return new UsernamePasswordAuthenticationToken(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getName()))
                        .collect(Collectors.toList()));
    }
}
