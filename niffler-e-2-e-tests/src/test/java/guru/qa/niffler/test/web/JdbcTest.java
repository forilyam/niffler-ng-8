package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.extension.InjectClientExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Date;
import java.util.Optional;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(InjectClientExtension.class)
public class JdbcTest {

  private UsersClient usersClient;
  private SpendClient spendClient;

  @Test
  void createSpendTest() {
    String username = "duck-15";
    SpendJson spend = spendClient.create(
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

    Optional<SpendJson> createdSpend = spendClient.findByUsernameAndSpendDescription(spend.username(), spend.description());
    assertTrue(createdSpend.isPresent());

    Optional<CategoryJson> createdCategory = spendClient.findCategoryByUsernameAndCategoryName(spend.username(), spend.category().name());
    assertTrue(createdCategory.isPresent());
  }

  @ValueSource(strings = {
      "valentin-12"
  })
  @ParameterizedTest
  void createUserTest(String uname) {

    UserJson user = usersClient.createUser(
        uname,
        "12345"
    );

    usersClient.createIncomeInvitations(user, 1);
    usersClient.createOutcomeInvitations(user, 1);
    usersClient.createFriends(user, 1);
  }

}