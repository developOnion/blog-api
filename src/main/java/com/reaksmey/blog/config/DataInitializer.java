package com.reaksmey.blog.config;

import com.reaksmey.blog.model.Blog;
import com.reaksmey.blog.model.BlogStatus;
import com.reaksmey.blog.model.User;
import com.reaksmey.blog.repository.BlogRepository;
import com.reaksmey.blog.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

	private final UserRepository userRepository;
	private final BlogRepository blogRepository;
	private final PasswordEncoder passwordEncoder;

	public DataInitializer(
		UserRepository userRepository,
		BlogRepository blogRepository,
		PasswordEncoder passwordEncoder
	) {

		this.userRepository = userRepository;
		this.blogRepository = blogRepository;
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
