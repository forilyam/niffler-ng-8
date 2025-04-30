package guru.qa.niffler.test.web;

import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import guru.qa.niffler.service.impl.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.Optional;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

//@Disabled
public class JdbcTest {

  @Test
  void createSpendTest() {
    SpendDbClient spendDbClient = new SpendDbClient();
    String username = "duck-15";
    SpendJson spend = spendDbClient.create(
        new SpendJson(
            null,
            new Date(),
            new CategoryJson(
                null,
                randomCategoryName(),
                username,
                false
            ),
            CurrencyValues.RUB,
            100.0,
            "description",
            username
        )
    );
    assertThat(spend).isNotNull();

    Optional<SpendJson> createdSpend = spendDbClient.findByUsernameAndSpendDescription(spend.username(), spend.description());
    assertTrue(createdSpend.isPresent());

    Optional<CategoryJson> createdCategory = spendDbClient.findCategoryByUsernameAndCategoryName(spend.username(), spend.category().name());
    assertTrue(createdCategory.isPresent());
  }

  @ValueSource(strings = {
      "valentin-12"
  })
  @ParameterizedTest
  void createUserTest(String uname) {
    UsersDbClient usersDbClient = new UsersDbClient();

    UserJson user = usersDbClient.createUser(
        uname,
        "12345"
    );

    usersDbClient.createIncomeInvitations(user, 1);
    usersDbClient.createOutcomeInvitations(user, 1);
    usersDbClient.createFriends(user, 1);
  }

}