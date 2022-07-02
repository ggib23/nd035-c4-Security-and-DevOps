package com.example.demo.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.model.requests.ModifyCartRequest;

public class OrderControllerTest {

    private UserController userController;

    private OrderController orderController;

    private CartController cartController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        orderController = new OrderController();
        cartController = new CartController();
        // Inject the required Autowired objects into the instance
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
        // Inject the required Autowired objects into the instance
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        // Inject the required Autowired objects into the instance
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void submit_order_happy_path() throws Exception {
        // First a user needs to exist and a cart needs to be created
        User user = createUser();
        String username = user.getUsername();

        // Mock return value
        when(userRepository.findByUsername(username)).thenReturn(user);

        Cart cart = createCart(); // Create cart and then submit cart
        final ResponseEntity<UserOrder> response = orderController.submit("test");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder testOrder = response.getBody();
        assertNotNull(testOrder);
    }

    @Test 
    public void get_order_history_for_user() throws Exception {
        // First a user needs to exist and a cart needs to be created
        User user = createUser();
        String username = user.getUsername();

        // Mock return values
        when(userRepository.findByUsername(username)).thenReturn(user);

        Cart cart = createCart(); // Create cart and then submit cart
        ResponseEntity<UserOrder> order = orderController.submit("test");
        UserOrder submittedOrder = order.getBody();
        List<UserOrder> userOrders = addOrder(user, submittedOrder);

        // Mock return values
        when(orderRepository.findByUser(user)).thenReturn(userOrders);

        // Once cart is submitted, return order history
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(userOrders, response.getBody());
    }

    public User createUser() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("test");
        request.setPassword("testPassword123");
        request.setConfirmPassword("testPassword123");
        final ResponseEntity<User> response = userController.createUser(request);

        User user = response.getBody();
        return user;
    }

    public Cart createCart() {
        // Setup an item
        Optional<Item> item = Optional.of(new Item());
        item.get().setPrice(BigDecimal.valueOf(1.99));

        // Create cart request
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("test");
        request.setItemId(1);
        request.setQuantity(1);
        
        // Mock return value
        when(itemRepository.findById(request.getItemId())).thenReturn(item);
        final ResponseEntity<Cart> response = cartController.addTocart(request);

        Cart cart = response.getBody();
        return cart;
    }

    public List<UserOrder> addOrder(User user, UserOrder userOrder) {
        List<UserOrder> list = orderRepository.findByUser(user);
		if(list == null) {
			list = new ArrayList<>();
		} 

		list.add(userOrder);
        return list;
	}
}