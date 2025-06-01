package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class SuccessfulRegistrationPage {
  private final SelenideElement congratsField = $("p.form__paragraph_success");
  private final SelenideElement signInBtn = $(".form_sign-in");

  @Step("Check successful registration")
  @Nonnull
  public SuccessfulRegistrationPage checkMessageOfSuccessfulRegistration(String message) {
    congratsField.shouldHave(text(message));
    return this;
  }

  @Step("Sign in")
  @Nonnull
  public LoginPage signIn() {
    signInBtn.click();
    return new LoginPage();
  }
}
