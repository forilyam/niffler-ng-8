package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class LoginPage {
  private final String VALIDATION_MESSAGE = "Заполните это поле.";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement submitBtn = $("button[type='submit']");
  private final SelenideElement registerBtn = $(".form__register");
  private final SelenideElement userIncorrectText = $(".form__error");

  public MainPage doLogin(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    submitBtn.click();
    return new MainPage();
  }

  @Nonnull
  public RegisterPage doRegister() {
    registerBtn.click();
    return new RegisterPage();
  }

  @Step("Set username: '{0}'")
  @Nonnull
  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Set password: '{0}'")
  @Nonnull
  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Submit login")
  @Nonnull
  public LoginPage submitLogin() {
    submitBtn.click();
    return this;
  }

  @Step("Check username is filled")
  public void checkThatUsernameIsFilled() {
    usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
  }

  @Step("Check password is filled")
  public void checkThatPasswordIsFilled() {
    passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));

  }

  @Step("Check user is not correct")
  public void checkThatUserIsNotCorrect() {
    userIncorrectText.shouldHave(text("Неверные учетные данные пользователя"));
  }

}
