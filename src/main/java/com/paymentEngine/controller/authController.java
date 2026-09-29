package com.paymentEngine.controller;

import com.paymentEngine.dto.loginRequest;
import com.paymentEngine.dto.loginResponse;
import com.paymentEngine.entity.appUser;
import com.paymentEngine.repository.appUserRepository;
import com.paymentEngine.security.JwtBlackListService;
import com.paymentEngine.security.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")

public class authController {
    private final appUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtBlackListService jwtBlacklistService;

    public authController(
            appUserRepository appUserRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService, JwtBlackListService jwtBlacklistService) {

        this.appUserRepository = appUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @PostMapping("/register")
    public String register(
            @RequestBody loginRequest request) {

        if (appUserRepository
                .findByUsername(request.getUsername())
                .isPresent()) {

            return "Username already exists";
        }

        appUser user = new appUser();

        user.setUsername(request.getUsername());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        user.setRole("USER");

        appUserRepository.save(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public loginResponse login(
            @RequestBody loginRequest request) {

        appUser user = appUserRepository
                .findByUsername(request.getUsername())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Invalid username or password"
                        )
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword())) {

            throw new RuntimeException(
                    "Invalid username or password"
            );
        }


        String token =
                jwtService.generateToken(
                        user.getUsername(),
                        user.getRole()
                );

        return new loginResponse(token);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestHeader("Authorization") String authorization) {

        if (authorization == null ||
                !authorization.startsWith("Bearer ")) {

            return ResponseEntity
                    .badRequest()
                    .body("Bearer token is required");
        }

        String token =
                authorization.substring(7);

        Date expiration =
                jwtService.extractExpiration(token);

        jwtBlacklistService.blacklistToken(
                token,
                expiration
        );

        return ResponseEntity.ok(
                "Logout successful. JWT has been revoked."
        );
    }

}
