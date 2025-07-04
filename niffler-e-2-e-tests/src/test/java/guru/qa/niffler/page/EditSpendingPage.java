package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.page.component.Calendar;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class EditSpendingPage extends BasePage<EditSpendingPage> {

  public static final String URL = CFG.frontUrl() + "spending";

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement sumInput = $("#amount");
  private final SelenideElement categoryInput = $("#category");

  private final Calendar calendar = new Calendar();

  @Step("Check that the page is loaded")
  @Override
  @Nonnull
  public EditSpendingPage checkThatPageLoaded() {
    amountInput.should(visible);
    return this;
  }

  @Step("Set new spending description: '{0}'")
  public MainPage editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    submitBtn.click();
    return new MainPage();
  }

  @Step("Edit spending amount: '{0}'")
  @Nonnull
  public MainPage editSpendingAmount(String amount) {
    amountInput.clear();
    amountInput.setValue(amount);
    submitBtn.click();
    return new MainPage();
  }

  @Step("Add new spending")
  @Nonnull
  public MainPage addNewSpending(SpendJson spend) {
    sumInput.setValue(String.valueOf(spend.amount())).pressEnter();
    categoryInput.setValue(spend.category().name()).pressEnter();
    calendar.selectDateInCalendar(spend.spendDate());
    descriptionInput.setValue(spend.description()).pressEnter();
    return new MainPage();
  }
}
