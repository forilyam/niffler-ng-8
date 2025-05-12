package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {

  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement submitBtn = $("#save");
  private final SelenideElement amountInput = $("#amount");

  public void editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    submitBtn.click();
  }

  public MainPage editSpendingAmount(String amount) {
    amountInput.clear();
    amountInput.setValue(amount);
    submitBtn.click();
    return new MainPage();
  }
}
