package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private static final long USER_ID = 1;
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SHORT_PASSWORD = "pass";
    private static final String OTHER_PASSWORD = "otherPassword";
    private static final String HASHED_PASSWORD = "hashedPassword";

    private UserController userController;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Before
    public void setUp() {
        userController = new UserController();

        CartRepository cartRepository = mock(CartRepository.class);
        userRepository = mock(UserRepository.class);
        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void it_should_create_a_new_user() {
        when(bCryptPasswordEncoder.encode(PASSWORD)).thenReturn(HASHED_PASSWORD);

        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        request.setConfirmPassword(PASSWORD);

        ResponseEntity<User> response = userController.createUser(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        User newUser = response.getBody();

        Assert.assertNotNull(newUser);
        Assert.assertEquals(request.getUsername(), newUser.getUsername());
        Assert.assertEquals(0, newUser.getId());
        Assert.assertEquals(HASHED_PASSWORD, newUser.getPassword());
    }

    @Test
    public void it_should_return_bad_request_if_confirm_password_is_different_from_password() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(USERNAME);
        request.setPassword(PASSWORD);
        request.setConfirmPassword(OTHER_PASSWORD);

        ResponseEntity<User> response = userController.createUser(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());

        User newUser = response.getBody();

        Assert.assertNull(newUser);
    }

    @Test
    public void it_should_return_bad_request_if_password_is_too_short() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername(USERNAME);
        request.setPassword(SHORT_PASSWORD);
        request.setConfirmPassword(SHORT_PASSWORD);

        ResponseEntity<User> response = userController.createUser(request);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());

        User newUser = response.getBody();

        Assert.assertNull(newUser);
    }

    @Test
    public void it_should_find_a_user_by_id() {
        Optional<User> user = Optional.of(createUser());

        when(userRepository.findById(USER_ID)).thenReturn(user);

        ResponseEntity<User> response = userController.findById(USER_ID);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_return_not_found_response_when_user_does_not_exist() {
        Optional<User> user = Optional.empty();
        when(userRepository.findById(USER_ID)).thenReturn(user);

        ResponseEntity<User> response = userController.findById(USER_ID);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_find_a_user_by_username() {
        User user = createUser();

        when(userRepository.findByUsername(USERNAME)).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName(USERNAME);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_return_not_found_response_when_user_does_not_exist_searching_by_username() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);

        ResponseEntity<User> response = userController.findByUserName(USERNAME);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    private User createUser() {
        return TestUtils.createUser(USER_ID, USERNAME, HASHED_PASSWORD);
    }
}
