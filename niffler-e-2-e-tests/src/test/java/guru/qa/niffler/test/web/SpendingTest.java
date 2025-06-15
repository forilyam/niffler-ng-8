package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.condition.Color;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.*;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

@ExtendWith(BrowserExtension.class)
public class SpendingTest {

  @User(
      spendings = @Spending(
          category = "Обучение",
          description = "Обучение Niffler 2.0",
          amount = 89000.00,
          currency = CurrencyValues.RUB
      )
  )
  @ApiLogin
  @Test
  void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
    final String newDescription = "Обучение Niffler NG";

    Selenide.open(MainPage.URL, MainPage.class)
        .getSpendingTable()
        .editSpending(user.testData().spendings().getFirst().description())
        .editDescription(newDescription)
        .getSpendingTable()
        .checkTableContains(newDescription);
  }

  @User(
      spendings = {
          @Spending(
              category = "Обучение",
              description = "Обучение Advanced 2.0",
              amount = 79990
          ),
          @Spending(
              category = "Техника",
              description = "Телевизор",
              amount = 95000.00,
              currency = CurrencyValues.RUB
          )}
  )
  @ApiLogin
  @ScreenShotTest(value = "img/expected-stat.png")
  void checkStatComponentTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(MainPage.URL, MainPage.class)
        .getStatComponent()
        .checkStatisticImage(expected)
        .checkStatBubblesInAnyOrder(
            new Bubble(Color.green, "Обучение 79990 ₽"),
            new Bubble(Color.yellow, "Техника 95000 ₽"));
  }

  @User(
      spendings = {
          @Spending(
              category = "Обучение",
              description = "Обучение Advanced 2.0",
              amount = 79990
          ),
          @Spending(
              category = "Техника",
              description = "Телевизор",
              amount = 95000.00,
              currency = CurrencyValues.RUB
          )}
  )
  @ApiLogin
  @Test
  void checkSpendingTableTest(UserJson user) {
    Selenide.open(MainPage.URL, MainPage.class)
        .getSpendingTable()
        .checkSpendTable(user.testData().spendings().toArray(SpendJson[]::new));
  }

  @User(
      spendings = {
          @Spending(
              category = "Продукты",
              description = "Хлебушек",
              amount = 100.00,
              currency = CurrencyValues.RUB
          )
      })
  @ApiLogin
  @ScreenShotTest(value = "img/expected-stat-edit.png")
  void checkStatComponentAfterEditingTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(MainPage.URL, MainPage.class)
        .getSpendingTable()
        .editSpending(user.testData().spendings().getFirst().description())
        .editSpendingAmount("95")
        .getStatComponent()
        .checkStatisticBubblesContains("Продукты 95 ₽")
        .checkStatisticImage(expected);
  }

  @User(
      spendings = @Spending(
          category = "Продукты",
          description = "Молоко",
          amount = 150.00,
          currency = CurrencyValues.RUB
      ))
  @ApiLogin
  @ScreenShotTest(value = "img/expected-stat-delete.png")
  void checkStatComponentAfterDeletingTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(MainPage.URL, MainPage.class)
        .getSpendingTable()
        .deleteSpending(user.testData().spendings().getFirst().description())
        .getStatComponent()
        .checkStatisticImage(expected);
  }

  @User(
      categories = {
          @Category(name = "Одежда"),
          @Category(name = "Домашние животные", archived = true),
          @Category(name = "Отдых", archived = true)
      },
      spendings = {
          @Spending(
              category = "Одежда",
              description = "Куртка",
              amount = 5000
          ),
          @Spending(
              category = "Домашние животные",
              description = "Корм",
              amount = 3000
          ),
          @Spending(
              category = "Отдых",
              description = "Кинотеатр",
              amount = 800
          )
      }
  )
  @ApiLogin
  @ScreenShotTest(value = "img/expected-stat-archived.png")
  void checkStatComponentArchivedCategoriesTest(UserJson user, BufferedImage expected) throws IOException {
    Selenide.open(MainPage.URL, MainPage.class)
        .getStatComponent()
        .checkStatisticBubblesContains("Одежда 5000 ₽", "Archived 3800 ₽")
        .checkStatisticImage(expected);
  }

  @User
  @ApiLogin
  @Test
  void shouldAddNewSpending(UserJson user) {
    SpendJson newSpend = new SpendJson(
        null,
        new Date(),
        new CategoryJson(
            null,
            "newCategory",
            user.username(),
            false
        ),
        CurrencyValues.RUB,
        100.0,
        RandomDataUtils.randomSentence(3),
        user.username()
    );

    Selenide.open(MainPage.URL, MainPage.class)
        .getHeader()
        .addSpendingPage()
        .addNewSpending(newSpend)
        .checkAlertMessage("New spending is successfully created")
        .getSpendingTable()
        .checkSpendTable(newSpend);
  }
}
