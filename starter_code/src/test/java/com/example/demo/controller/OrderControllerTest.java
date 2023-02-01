package com.example.demo.controller;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

public class OrderControllerTest {
  private OrderController orderController;
  private UserRepository userRepo = mock(UserRepository.class);
  private OrderRepository orderRepo = mock(OrderRepository.class);

  @Before
  public void setUp() {
    orderController = new OrderController();
    TestUtils.injectObjects(orderController, "userRepository", userRepo);
    TestUtils.injectObjects(orderController, "orderRepository", orderRepo);
  }

  @Test
  public void submit_order_happy_path(){

    Item item = new Item();
    item.setId(1L);
    item.setName("Test Item");
    BigDecimal price = BigDecimal.valueOf(2.99);
    item.setPrice(price);
    List<Item> items = new ArrayList<Item>();
    items.add(item);

    User user = new User(0,"test","testPassword");
    Cart cart = new Cart();
    cart.setId(0L);
    cart.setUser(user);
    cart.setItems(items);
    BigDecimal total = BigDecimal.valueOf(2.99);
    cart.setTotal(total);
    user.setCart(cart);

    UserOrder userOrder = UserOrder.createFromCart(cart);

    when(userRepo.findByUsername("test")).thenReturn(user);
    when(orderRepo.save(any())).thenReturn(userOrder);

    ResponseEntity<UserOrder> response = orderController.submit("test");
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    UserOrder order = response.getBody();
    assertNotNull(order);
    assertEquals(userOrder.getItems().size(), order.getItems().size());
    assertEquals(userOrder.getUser(), order.getUser());

  }
  @Test
  public void submit_order_user_non_existent(){
    ResponseEntity<UserOrder> response = orderController.submit("test");
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void get_orders_for_user_happy_path(){
    when(userRepo.findByUsername("test")).thenReturn(new User());
    when(orderRepo.findByUser(any())).thenReturn(Collections.singletonList(new UserOrder()));
    ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
    List<UserOrder> orders = response.getBody();
    assertNotNull(orders);
  }
  @Test
  public void get_orders_for_user_name_non_existent(){
    ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
    assertEquals(404, response.getStatusCodeValue());
  }
  @Test
  public void get_orders_for_user_non_existent(){
    when(userRepo.findByUsername("test")).thenReturn(new User());
    ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");
    assertEquals(0,response.getBody().size());
    assertEquals(200, response.getStatusCodeValue());
  }
}
