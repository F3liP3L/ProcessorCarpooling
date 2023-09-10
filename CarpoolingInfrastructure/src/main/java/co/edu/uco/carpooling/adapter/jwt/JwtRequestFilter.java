package co.edu.uco.carpooling.adapter.jwt;

import co.edu.uco.carpooling.adapter.jwt.component.JwtTokenServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenServiceImpl jwtTokenUtil;
    private final UserAuthenticationService userAuthenticationDetails;

    public JwtRequestFilter(JwtTokenServiceImpl jwtTokenUtil, UserAuthenticationService userAuthenticationDetails) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.userAuthenticationDetails = userAuthenticationDetails;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request
            , @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);
        userEmail = jwtTokenUtil.extractUserName(jwt);
        if (userEmail.isEmpty()
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userAuthenticationDetails.userDetailsService()
                    .loadUserByUsername(userEmail);
            if (jwtTokenUtil.isTokenValid(jwt, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
            }
        }
        filterChain.doFilter(request, response);
    }
}
