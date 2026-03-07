package com.reaksmey.blog.config;

import com.reaksmey.blog.model.user.User;
import com.reaksmey.blog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) throws Exception {

		if (userRepository.findByUsername("admin").isEmpty()) {

			User admin = new User(
				"admin",
				passwordEncoder.encode("admin123")
			);

			userRepository.save(admin);
		}
	}
}
