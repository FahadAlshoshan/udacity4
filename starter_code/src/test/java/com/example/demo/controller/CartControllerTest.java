package com.example.demo.controller;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class CartControllerTest {

  private CartController cartController;
  private UserRepository userRepo = mock(UserRepository.class);
  private CartRepository cartRepo = mock(CartRepository.class);
  private ItemRepository itemRepo = mock(ItemRepository.class);

  @Before
  public void setup() {
    cartController = new CartController();
    TestUtils.injectObjects(cartController, "userRepository", userRepo);
    TestUtils.injectObjects(cartController, "cartRepository", cartRepo);
    TestUtils.injectObjects(cartController, "itemRepository", itemRepo);
  }

  @Test
  public void add_to_cart_happy_path() {
    Item item = new Item();
    item.setId(1L);
    item.setName("Test Item");
    BigDecimal price = BigDecimal.valueOf(2.99);
    item.setPrice(price);
    List<Item> items = new ArrayList<Item>();
    items.add(item);

    User user = new User(0, "test", "testPassword");
    Cart cart = new Cart();
    user.setCart(cart);

    ModifyCartRequest r = new ModifyCartRequest();
    r.setItemId(1L);
    r.setQuantity(1);
    r.setUsername("test");

    when(userRepo.findByUsername(any())).thenReturn(user);
    when(itemRepo.findById(1L)).thenReturn(Optional.of(item));
    ResponseEntity<Cart> response = cartController.addTocart(r);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    Cart c = response.getBody();
    assertNotNull(c);
    assertEquals(BigDecimal.valueOf(2.99), c.getTotal());
  }

  @Test
  public void add_to_cart_user_non_existent() {
    ModifyCartRequest r = new ModifyCartRequest();
    r.setItemId(1L);
    r.setQuantity(1);
    r.setUsername("test");

    ResponseEntity<Cart> response = cartController.addTocart(r);

    assertNull(response.getBody());
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void add_to_cart_item_non_existent() {
    ModifyCartRequest r = new ModifyCartRequest();
    r.setItemId(1L);
    r.setQuantity(1);
    r.setUsername("test");

    when(userRepo.findByUsername(any())).thenReturn(new User());
    ResponseEntity<Cart> response = cartController.addTocart(r);

    assertNull(response.getBody());
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void remove_from_cart_happy_path() {
    Item item = new Item();
    item.setId(1L);
    item.setName("Test Item");
    BigDecimal price = BigDecimal.valueOf(2.99);
    item.setPrice(price);

    User user = new User(0, "test", "testPassword");
    Cart cart = new Cart();
    cart.addItem(item);
    cart.addItem(item);
    cart.setTotal(BigDecimal.valueOf(5.98));
    user.setCart(cart);

    assertEquals(cart.getTotal(), BigDecimal.valueOf(5.98));

    when(userRepo.findByUsername(any())).thenReturn(user);
    when(itemRepo.findById(1L)).thenReturn(Optional.of(item));

    ModifyCartRequest r = new ModifyCartRequest();
    r.setItemId(1L);
    r.setQuantity(1);
    r.setUsername("test");

    ResponseEntity<Cart> response = cartController.removeFromcart(r);

    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());

    assertNotNull(response.getBody());
    assertEquals(BigDecimal.valueOf(2.99), response.getBody().getTotal());
  }
}
