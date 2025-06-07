package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UsersApiTest {

  private final UsersApiClient usersApiClient = new UsersApiClient();

  @Order(1)
  @User
  @Test
  void emptyDbTest(UserJson user) {
    List<UserJson> allUsers = usersApiClient.allUsers(user.username(), null);
    Assertions.assertTrue(allUsers.isEmpty());
  }

  @Order(Integer.MAX_VALUE)
  @User
  @Test
  void notEmptyDbTest(UserJson user) {
    List<UserJson> allUsers = usersApiClient.allUsers(user.username(), null);
    Assertions.assertFalse(allUsers.isEmpty());
  }
}
