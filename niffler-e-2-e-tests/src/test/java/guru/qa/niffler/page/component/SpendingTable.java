package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.page.EditSpendingPage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.SpendConditions.spends;

@ParametersAreNonnullByDefault
public class SpendingTable extends BaseComponent<SpendingTable> {

  private final SearchField searchField = new SearchField();
  private final SelenideElement deleteBtn = self.$("#delete");
  private final SelenideElement dialogWindow = $("div[role='dialog']");
  private final ElementsCollection tableRows = self.$("tbody").$$("tr");

  public SpendingTable() {
    super($("#spendings"));
  }

  @Step("Edit spending with description '{0}'")
  @Nonnull
  public EditSpendingPage editSpending(String description) {
    searchSpendingByDescription(description);
    SelenideElement row = tableRows.find(text(description));
    row.$$("td").get(5).click();
    return new EditSpendingPage();
  }

  @Step("Delete spending with description '{0}'")
  @Nonnull
  public MainPage deleteSpending(String description) {
    searchSpendingByDescription(description);
    tableRows.find(text(description))
        .$$("td")
        .get(0)
        .click();
    deleteBtn.click();
    dialogWindow.$(byText("Delete")).click();
    return new MainPage();
  }

  @Step("Search spending with description '{0}'")
  @Nonnull
  public SpendingTable searchSpendingByDescription(String description) {
    searchField.search(description);
    return this;
  }

  @Step("Check that table contains '{0}'")
  @Nonnull
  public SpendingTable checkTableContains(String expectedSpend) {
    searchSpendingByDescription(expectedSpend);
    tableRows.find(text(expectedSpend)).should(visible);
    return this;
  }

  @Step("Check spending table")
  public SpendingTable checkSpendTable(SpendJson... expectedSpends) {
    tableRows.shouldHave(spends(expectedSpends));
    return this;
  }
}