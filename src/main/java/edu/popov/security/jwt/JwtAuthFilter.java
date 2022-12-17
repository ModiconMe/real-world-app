package edu.popov.security.jwt;

import edu.popov.security.AuthenticationProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class JwtAuthFilter extends GenericFilter {

    private static final String TOKEN_PREFIX = "Token ";
    private final JwtUtils jwtUtils;
    private final AuthenticationProvider authenticationProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        Optional.ofNullable(((HttpServletRequest)request).getHeader(HttpHeaders.AUTHORIZATION)) // get auth header
                .filter(authHeader -> authHeader.startsWith(TOKEN_PREFIX)) // search for bearer token
                .map(authHeader -> authHeader.substring(TOKEN_PREFIX.length())) // remove Bearer prefix
                .filter(jwtUtils::isTokenValid) // check that token is not expired and username (email) is exists
                .map(jwtUtils::extractUsername) // get username from token
                .map(authenticationProvider::getAuthentication) // authenticate user
                .ifPresent(SecurityContextHolder.getContext()::setAuthentication); // set authentication token to context
        chain.doFilter(request, response);
    }

}
