package com.reaksmey.blog.config;

import com.reaksmey.blog.auth.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	public JwtFilter(
		JwtService jwtService,
		UserDetailsServiceImpl userDetailsService,
		JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
	) {

		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
	}

	@Override
	protected void doFilterInternal(
		@NonNull HttpServletRequest request,
		@NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain
	) throws ServletException, IOException {

//		final String path = request.getServletPath();
//		log.info("Incoming request to {}", path);
//
//		if (path.startsWith("/auth")
//			|| path.startsWith("/v3/api-docs")
//			|| path.startsWith("/swagger-ui")
//			|| path.equals("/swagger-ui.html")
//		) {
//			log.info("Request to {} - skipping JWT filter", request.getServletPath());
//			log.info("Skipping JWT filter for auth endpoint");
//			filterChain.doFilter(request, response);
//			return;
//		}

		final String authHeader = request.getHeader("Authorization");
		final String refreshToken;
		final String username;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.info("Missing or invalid Authorization header");
			filterChain.doFilter(request, response);
			return;
		}

		try {
			refreshToken = authHeader.substring(7);
			username = jwtService.extractUsername(refreshToken);

			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				if (jwtService.isValidToken(refreshToken, userDetails)) {

					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
					);
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

			filterChain.doFilter(request, response);
		} catch (JwtException e) {
			SecurityContextHolder.clearContext();
			String clientMessage = mapExceptionToClientMessage(e);
			log.debug("JWT error: {}", e.getMessage());
			jwtAuthenticationEntryPoint.commence(request, response, new AuthenticationException(clientMessage) {});
		}
	}

	private String mapExceptionToClientMessage(JwtException e) {
		return switch (e) {
			case ExpiredJwtException expiredJwtException -> "JWT expired";
			case SignatureException signatureException -> "Invalid JWT signature";
			case MalformedJwtException malformedJwtException -> "Malformed JWT";
			case null, default -> "Invalid token";
		};
	}
}
