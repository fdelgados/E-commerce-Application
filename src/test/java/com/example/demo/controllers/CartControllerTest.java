package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.mockito.Mockito.*;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository;
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        cartController = new CartController();

        CartRepository cartRepository = mock(CartRepository.class);
        userRepository = mock(UserRepository.class);
        itemRepository = mock(ItemRepository.class);

        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void it_should_add_an_item_to_cart() {
        given_the_user_exists();
        given_the_item_exists();

        ResponseEntity<Cart> response = cartController.addTocart(createCartRequest());
        Cart cart = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull(cart);
    }

    @Test
    public void it_should_not_add_item_to_cart_if_user_does_not_exists() {
        given_the_user_does_not_exist();

        ResponseEntity<Cart> response = cartController.addTocart(createCartRequest());
        Cart cart = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        Assert.assertNull(cart);
    }

    @Test
    public void it_should_not_add_item_to_cart_if_item_does_not_exists() {
        given_the_user_exists();
        given_the_item_does_not_exist();

        ResponseEntity<Cart> response = cartController.addTocart(createCartRequest());
        Cart cart = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        Assert.assertNull(cart);
    }

    @Test
    public void it_should_remove_an_item_from_cart() {
        given_the_user_exists();
        given_the_item_exists();

        ResponseEntity<Cart> response = cartController.removeFromcart(createCartRequest());
        Cart cart = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        Assert.assertNotNull(cart);
    }

    @Test
    public void it_should_not_remove_an_item_from_if_user_does_not_exists() {
        given_the_user_does_not_exist();

        ResponseEntity<Cart> response = cartController.removeFromcart(createCartRequest());
        Cart cart = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        Assert.assertNull(cart);
    }

    @Test
    public void it_should_not_remove_an_item_from_if_item_does_not_exists() {
        given_the_user_exists();
        given_the_item_does_not_exist();

        ResponseEntity<Cart> response = cartController.removeFromcart(createCartRequest());
        Cart cart = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        Assert.assertNull(cart);
    }

    private ModifyCartRequest createCartRequest() {
        ModifyCartRequest cartRequest = new ModifyCartRequest();

        cartRequest.setUsername("username");
        cartRequest.setItemId(1);
        cartRequest.setQuantity(1);

        return cartRequest;
    }

    private void given_the_user_exists() {
        ModifyCartRequest cartRequest = createCartRequest();

        when(userRepository.findByUsername(cartRequest.getUsername()))
                .thenReturn(TestUtils.createUser(cartRequest.getUsername()));
    }

    private void given_the_user_does_not_exist() {
        ModifyCartRequest cartRequest = createCartRequest();

        when(userRepository.findByUsername(cartRequest.getUsername())).thenReturn(null);
    }

    private void given_the_item_exists() {
        ModifyCartRequest cartRequest = createCartRequest();
        Optional<Item> item = Optional.of(TestUtils.createItem(cartRequest.getItemId()));

        when(itemRepository.findById(cartRequest.getItemId()))
                .thenReturn(item);
    }

    private void given_the_item_does_not_exist() {
        ModifyCartRequest cartRequest = createCartRequest();
        Optional<Item> item = Optional.empty();

        when(itemRepository.findById(cartRequest.getItemId()))
                .thenReturn(item);
    }
}
