package emanuelepiemonte.Trackfolio.security;

import emanuelepiemonte.Trackfolio.entities.User;
import emanuelepiemonte.Trackfolio.exceptions.UnauthorizedException;
import emanuelepiemonte.Trackfolio.services.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {
    private final TokenTools tokenTools;
    private final UserService userService;

    public TokenFilter(TokenTools tokenTools, UserService userService) {
        this.tokenTools = tokenTools;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Verifico se la richiesta contiene l'header Authorization "Bearer .."
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer "))
            throw new UnauthorizedException("Inserire il token nell'authorization header nel formato corretto");

        // 2. Estraggo il token dall'header
        String accessToken = authHeader.replace("Bearer ", "");

        // 3. Verifico che il token sia OK (la firma e che non sia scaduto)
        tokenTools.verifyToken(accessToken);

        // -----------> AUTORIZZAZIONE <-------
        UUID userId = this.tokenTools.extractIdFromToken(accessToken);

        User authenticatedUser = this.userService.findById(userId);

        Authentication authentication = new UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String path = request.getServletPath();
        String method = request.getMethod();

        if (pathMatcher.match("/tv_series", path) && "POST".equalsIgnoreCase(method)) {
            return false;
        }
        if (pathMatcher.match("/tv_series/watch", path) || pathMatcher.match("/tv_series/watch/**", path)) {
            return false;
        }
        return pathMatcher.match("/auth/**", path) ||
                pathMatcher.match("/movies/**", path) ||
                pathMatcher.match("/tv_series/**", path) ||
                pathMatcher.match("/anime/**", path);
    }

}
