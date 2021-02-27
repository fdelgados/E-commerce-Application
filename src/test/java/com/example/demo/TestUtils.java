package com.example.demo;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class TestUtils {
    public static void injectObjects(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;

        try {
            Field field = target.getClass().getDeclaredField(fieldName);

            if (!field.isAccessible()) {
                field.setAccessible(true);
                wasPrivate = true;
            }

            field.set(target, toInject);

            if (wasPrivate) {
                field.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static User createUser(long userId, String username, String password) {
        User user = new User();

        user.setId(userId);
        user.setUsername(username);
        user.setPassword(password);

        return user;
    }

    public static User createUser(String username) {
        User user = new User();

        user.setId(1);
        user.setUsername(username);
        user.setPassword("password");

        Cart cart = createCart(1);
        cart.setUser(user);

        user.setCart(cart);

        return user;
    }

    public static Item createItem(long id) {
        Item item = new Item();

        item.setId(id);
        item.setName("Item name");
        item.setPrice(BigDecimal.valueOf(9.99));
        item.setDescription("Item description");

        return item;
    }

    public static Cart createCart(long id) {
        Cart cart = new Cart();

        cart.setId(id);
        cart.addItem(createItem(1));

        return cart;
    }
}
