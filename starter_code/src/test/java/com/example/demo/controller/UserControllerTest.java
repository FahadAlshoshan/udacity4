package com.example.demo.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserControllerTest {

  private UserController userController;
  private UserRepository userRepo = mock(UserRepository.class);
  private CartRepository cartRepo = mock(CartRepository.class);
  private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

  @Before
  public void setUp() {
    userController = new UserController();
    TestUtils.injectObjects(userController, "userRepository", userRepo);
    TestUtils.injectObjects(userController, "cartRepository", cartRepo);
    TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
  }

  @Test
  public void create_user_happy_path() {
    when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
    CreateUserRequest r = new CreateUserRequest();
    r.setUsername("test");
    r.setPassword("testPassword");
    r.setConfirmPassword("testPassword");
    final ResponseEntity<User> response = userController.createUser(r);
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    User u = response.getBody();
    assertNotNull(u);
    assertEquals(0, u.getId());
    assertEquals("test", u.getUsername());
    assertEquals("thisIsHashed", u.getPassword());
  }

  @Test
  public void create_user_short_password() {
    CreateUserRequest r = new CreateUserRequest();
    r.setUsername("test");
    r.setPassword("1234");
    r.setConfirmPassword("1234");
    final ResponseEntity<User> response = userController.createUser(r);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void create_user_password_mismatch() {
    CreateUserRequest r = new CreateUserRequest();
    r.setUsername("test");
    r.setPassword("password1");
    r.setConfirmPassword("password2");
    final ResponseEntity<User> response = userController.createUser(r);
    assertEquals(400, response.getStatusCodeValue());
  }

  @Test
  public void find_user_by_name_happy_path() {
    when(userRepo.findByUsername("test")).thenReturn(new User(0, "test", "testPassword"));
    final ResponseEntity<User> response = userController.findByUserName("test");
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    User u = response.getBody();
    assertNotNull(u);
    assertEquals("test", u.getUsername());
  }

  @Test
  public void find_user_by_name_non_existent() {
    when(userRepo.findByUsername("someone")).thenReturn(null);
    final ResponseEntity<User> response = userController.findByUserName("someone");
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }

  @Test
  public void find_user_by_id_happy_path() {
    when(userRepo.findById(0L)).thenReturn(Optional.of(new User(0, "test", "testPassword")));
    final ResponseEntity<User> response = userController.findById(0L);
    assertNotNull(response);
    assertEquals(200, response.getStatusCodeValue());
    User u = response.getBody();
    assertNotNull(u);
    assertEquals(0, u.getId());
    ;
  }

  @Test
  public void find_user_by_id_non_existent() {
    final ResponseEntity<User> response = userController.findById(1L);
    assertNotNull(response);
    assertEquals(404, response.getStatusCodeValue());
  }
}
