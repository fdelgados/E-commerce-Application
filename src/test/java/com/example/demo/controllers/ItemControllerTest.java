package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private static final String ITEM_NAME = "Item name";
    private static final int NUMBER_OF_ITEMS = 5;

    private ItemController itemController;
    private ItemRepository itemRepository;

    @Before
    public void setUp() {
        itemController = new ItemController();

        itemRepository = mock(ItemRepository.class);

        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void it_should_show_all_items() {
        given_there_are_items_in_the_catalog();

        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> items = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNotNull(items);
        Assert.assertEquals(NUMBER_OF_ITEMS, items.size());
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_not_show_items_if_catalog_is_empty() {
        given_the_catalog_is_empty();

        ResponseEntity<List<Item>> response = itemController.getItems();
        List<Item> items = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNotNull(items);
        Assert.assertEquals(0, items.size());
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_show_items_by_name() {
        given_items_have_been_found();

        ResponseEntity<List<Item>> response = itemController.getItemsByName(ITEM_NAME);
        List<Item> items = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNotNull(items);
        Assert.assertEquals(NUMBER_OF_ITEMS, items.size());
        Assert.assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void it_should_not_show_items_by_name_if_they_do_not_exist() {
        given_items_have_not_been_found();

        ResponseEntity<List<Item>> response = itemController.getItemsByName(ITEM_NAME);
        List<Item> items = response.getBody();

        Assert.assertNotNull(response);
        Assert.assertNull(items);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    private void given_items_have_been_found() {
        List<Item> items = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_ITEMS; i++) {
            items.add(TestUtils.createItem(i));
        }

        when(itemRepository.findByName(ITEM_NAME)).thenReturn(items);
    }

    private void given_items_have_not_been_found() {
        List<Item> items = new ArrayList<>();

        when(itemRepository.findByName(ITEM_NAME)).thenReturn(items);
    }

    private void given_there_are_items_in_the_catalog() {
        List<Item> items = new ArrayList<>();

        for (long i = 0; i < NUMBER_OF_ITEMS; i++) {
            items.add(TestUtils.createItem(i));
        }

        when(itemRepository.findAll()).thenReturn(items);
    }

    private void given_the_catalog_is_empty() {
        List<Item> items = new ArrayList<>();

        when(itemRepository.findAll()).thenReturn(items);
    }
}
