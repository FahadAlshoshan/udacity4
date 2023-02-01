package com.example.demo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class ItemControllerTest {
  private ItemController itemController;
  private ItemRepository itemRepo = mock(ItemRepository.class);

  @Before
  public void setUp() {
    itemController = new ItemController();
    TestUtils.injectObjects(itemController, "itemRepository", itemRepo);
  }

  @Test
  public void get_items_happy_path() {
    when(itemRepo.findAll()).thenReturn(Collections.singletonList(new Item()));
    ResponseEntity<List<Item>> response = itemController.getItems();
    assertEquals(1, response.getBody().size());
  }

  @Test
  public void get_item_by_id_happy_path() {
    when(itemRepo.findById(1L)).thenReturn(Optional.of(new Item()));
    ResponseEntity<Item> response = itemController.getItemById(1L);
    assertNotNull(response.getBody());
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void get_item_by_id_non_existent() {
    ResponseEntity<Item> response = itemController.getItemById(1L);
    assertNull(response.getBody());
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void get_items_by_name_happy_path() {
    when(itemRepo.findByName("test")).thenReturn(Collections.singletonList(new Item()));
    ResponseEntity<List<Item>> response = itemController.getItemsByName("test");
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals(200, response.getStatusCodeValue());
  }

  @Test
  public void get_items_by_name_non_existent() {
    ResponseEntity<List<Item>> response = itemController.getItemsByName("test");
    assertNull(response.getBody());
    assertEquals(404, response.getStatusCodeValue());
  }
}
