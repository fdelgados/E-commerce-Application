package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.mockito.Mockito.*;

public class OrderControllerTest {
    private static final String USERNAME = "username";

    private OrderController orderController;
    private UserRepository userRepository;

    @Before
    public void setUp() {
        orderController = new OrderController();

        OrderRepository orderRepository = mock(OrderRepository.class);
        userRepository = mock(UserRepository.class);

        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void it_should_submit_an_order() {
        given_the_user_exists();

        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        UserOrder order = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNotNull(order);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_not_submit_an_order_if_user_does_not_exist() {
        given_the_user_does_not_exist();

        ResponseEntity<UserOrder> response = orderController.submit(USERNAME);
        UserOrder order = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNull(order);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_show_user_purchase_history() {
        given_the_user_exists();

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);
        List<UserOrder> orders = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNotNull(orders);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_show_no_user_purchase_history_if_user_does_not_exist() {
        given_the_user_does_not_exist();

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(USERNAME);
        List<UserOrder> orders = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNull(orders);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    private void given_the_user_exists() {
        when(userRepository.findByUsername(USERNAME))
                .thenReturn(TestUtils.createUser(USERNAME));
    }

    private void given_the_user_does_not_exist() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(null);
    }
}
