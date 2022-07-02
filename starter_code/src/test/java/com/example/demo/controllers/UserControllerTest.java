package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        // Inject the required Autowired objects into the instance
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_happy_path() throws Exception {
       final ResponseEntity<User> response = createUser();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User testUser = response.getBody();

        assertNotNull(testUser);
        assertEquals(0, testUser.getId());
        assertEquals("test", testUser.getUsername());
        assertEquals("thisIsHashed", testUser.getPassword()); // Because of Stubbing above, you will test the value against "thisIsHashed"
    }

    @Test
    public void get_user_by_username() throws Exception {
        // Create user to retrieve
        final ResponseEntity<User> returnedUser = createUser();
        User user = returnedUser.getBody();

        // Stubbing
        when(userRepository.findByUsername("test")).thenReturn(user);

        // GET request to find user by username
        ResponseEntity<User> response = userController.findByUserName("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User testUser = response.getBody();

        assertNotNull(testUser);
        assertEquals(0, testUser.getId());
        assertEquals("test", testUser.getUsername());
    }

    public ResponseEntity<User> createUser() {
        // Stubbing
        when(bCryptPasswordEncoder.encode("testPassword123")).thenReturn("thisIsHashed"); // Anytime bCryptPasswordEncoder.encode("testPassword123") is called I can replace that value
        
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword123");
        request.setConfirmPassword("testPassword123");

        return userController.createUser(request);
    }
}
