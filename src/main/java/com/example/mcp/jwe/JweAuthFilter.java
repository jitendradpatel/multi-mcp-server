package com.example.mcp.jwe;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.nimbusds.jwt.JWTClaimsSet;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JweAuthFilter extends OncePerRequestFilter {

    private final JweUtils jweUtil;

    public JweAuthFilter(JweUtils jweUtil) {
        this.jweUtil = jweUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, jakarta.servlet.ServletException {
        String header = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + header);
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String token = header.substring(7);
                JWTClaimsSet claims = jweUtil.decryptToken(token);

                List<String> roles = claims.getStringListClaim("roles");
                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());

                var auth = new UsernamePasswordAuthenticationToken(
                        claims.getSubject(), null, authorities);

                SecurityContextHolder.getContext().setAuthentication(auth);

                System.out.println("Authenticated user: " + claims.getSubject() + " with roles: " + roles);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(request, response);
    }
}

