package com.reaksmey.blog.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reaksmey.blog.config.JwtAuthenticationEntryPoint;
import com.reaksmey.blog.config.JwtService;
import com.reaksmey.blog.exception.AuthenticationException;
import com.reaksmey.blog.exception.ResourceNotFoundException;
import com.reaksmey.blog.user.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.io.IOException;

@Slf4j
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final AuthenticationManager authManager;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	public AuthService(
		UserRepository userRepository,
		AuthenticationManager authManager,
		PasswordEncoder passwordEncoder,
		JwtService jwtService,
		JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
	) {

		this.userRepository = userRepository;
		this.authManager = authManager;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
	}

	public AuthResponse authenticate(AuthRequest loginRequest) {

		try {
			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				loginRequest.username(),
				loginRequest.password()
			);
			authManager.authenticate(authToken);

			var user = userRepository.findByUsername(loginRequest.username())
				.orElseThrow(() -> new AuthenticationException("User not found"));

			UserPrincipal principal = new UserPrincipal(user);
			var token = jwtService.generateToken(principal);
			var refreshToken = jwtService.generateRefreshToken(principal);

			return new AuthResponse(token, refreshToken);
		} catch (org.springframework.security.core.AuthenticationException e) {
			throw new AuthenticationException("Invalid username or password");
		}
	}

	public AuthResponse register(AuthRequest registerRequest) {
		return null;
	}

	public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {

		final String authHeader = request.getHeader("Authorization");
		final String refreshToken;
		final String username;

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.info("Missing or invalid Authorization header");
			return;
		}

		try {
			refreshToken = authHeader.substring(7);
			username = jwtService.extractUsername(refreshToken);

			if (username != null) {

				UserDetails user = new UserPrincipal(
					userRepository.findByUsername(username)
						.orElseThrow(() -> new ResourceNotFoundException("User does not exists"))
				);

				if (jwtService.isValidToken(refreshToken, user)) {
					var accessToken = jwtService.generateToken(user);
					var authResponse = new AuthResponse(
						accessToken,
						refreshToken
					);

					new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
				}
			}
		} catch (SignatureException | ExpiredJwtException | MalformedJwtException e) {
			SecurityContextHolder.clearContext();
			throw new AuthenticationException(e.getMessage()) {};
		}
	}
}
