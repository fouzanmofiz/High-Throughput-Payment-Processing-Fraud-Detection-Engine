package com.paymentEngine.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component


public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final userCustomerDetailsService userDetailsService;
    private final JwtBlackListService jwtBlacklistService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            userCustomerDetailsService userDetailsService, JwtBlackListService jwtBlacklistService) {

        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.jwtBlacklistService = jwtBlacklistService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println("######## JWT FILTER CALLED ########");
        System.out.println("URL = " + request.getRequestURI());

        String authHeader =
                request.getHeader("Authorization");

        System.out.println("===== JWT DEBUG =====");
        System.out.println("Request URL: " + request.getRequestURI());
        System.out.println("Authorization Header: " + authHeader);



        // No Authorization header
        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);
            return;
        }

        String token =
                authHeader.substring(7);

        System.out.println("Token received: " + token);

        if (jwtBlacklistService.isBlacklisted(token)) {

            System.out.println("JWT TOKEN IS BLACKLISTED");

            filterChain.doFilter(request, response);
            return;
        }

        try {

            String username =
                    jwtService.extractUsername(token);

            System.out.println("Username extracted from JWT: " + username);

            if (username != null &&
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService
                                .loadUserByUsername(username);

                if (jwtService.isTokenValid(token)) {

                    System.out.println("JWT IS VALID");
                    System.out.println(
                            "User authorities: "
                                    + userDetails.getAuthorities()
                    );

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authentication.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);

                    System.out.println(
                            "Authentication set successfully"
                    );
                }
            }

        } catch (Exception e) {

            System.out.println(
                    "JWT authentication failed: "
                            + e.getMessage()
            );
        }

        filterChain.doFilter(request, response);
    }

}
