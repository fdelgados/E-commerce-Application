package com.example.demo.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final int PASSWORD_MIN_LENGTH = 7;

	private static final Logger log = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@GetMapping("/id/{id}")
	public ResponseEntity<User> findById(@PathVariable Long id) {
		return ResponseEntity.of(userRepository.findById(id));
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<User> findByUserName(@PathVariable String username) {
		User user = userRepository.findByUsername(username);
		return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
	}
	
	@PostMapping("/create")
	public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
		User user = new User();
		user.setUsername(createUserRequest.getUsername());

		log.info("User name set with \"{}\"", createUserRequest.getUsername());

		Cart cart = new Cart();

		if (createUserRequest.getPassword().length() < PASSWORD_MIN_LENGTH) {
			log.error("Password is too short. Minimum length is {}", PASSWORD_MIN_LENGTH);

			return ResponseEntity.badRequest().build();
		}

		if (!createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
			log.error("Password and password confirm does not match");

			return ResponseEntity.badRequest().build();
		}
		user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));

		user.setCart(cart);
		cartRepository.save(cart);
		userRepository.save(user);

		log.info("User \"{}\" created successfully", createUserRequest.getUsername());

		return ResponseEntity.ok(user);
	}
	
}
