package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.component.Header;
import guru.qa.niffler.page.component.SpendingTable;
import guru.qa.niffler.page.component.StatComponent;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class MainPage {

  private final SelenideElement spendings = $("#spendings");

  protected final Header header = new Header();
  protected final SpendingTable spendingTable = new SpendingTable();
  protected final StatComponent statComponent = new StatComponent();

  @Nonnull
  public StatComponent getStatComponent() {
    return statComponent;
  }

  @Nonnull
  public Header getHeader() {
    return header;
  }

  @Nonnull
  public SpendingTable getSpendingTable() {
    return spendingTable;
  }

  @Step("Check Main page is loaded")
  public MainPage checkThatMainPageIsLoaded() {
    statComponent.self.shouldBe(visible).shouldHave(text("Statistics"));
    spendings.shouldBe(visible).shouldHave(text("History of Spendings"));
    return this;
  }
}
