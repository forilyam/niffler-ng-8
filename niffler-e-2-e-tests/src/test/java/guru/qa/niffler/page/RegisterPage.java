package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

@ParametersAreNonnullByDefault
public class RegisterPage extends BasePage<RegisterPage> {

  public static final String URL = CFG.authUrl() + "register";

  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
  private final SelenideElement signUpBtn = $("button[type='submit']");
  private final SelenideElement usernameErrorText = $("#username + .form__error");
  private final SelenideElement passwordErrorText = $x("//*[@id='password']/following-sibling::span");
  private final SelenideElement passwordSubmitErrorText = $x("//*[@id='passwordSubmit']/following-sibling::span");
  private final String VALIDATION_MESSAGE = "Заполните это поле.";

  @Step("Check that page is loaded")
  @Override
  @Nonnull
  public RegisterPage checkThatPageLoaded() {
    usernameInput.should(visible);
    passwordInput.should(visible);
    passwordSubmitInput.should(visible);
    return this;
  }

  @Step("Set username: '{0}'")
  @Nonnull
  public RegisterPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Set password: '{0}'")
  @Nonnull
  public RegisterPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Confirm password: '{0}'")
  @Nonnull
  public RegisterPage setPasswordSubmit(String password) {
    passwordSubmitInput.setValue(password);
    return this;
  }

  @Step("Submit registration form")
  @Nonnull
  public RegisterPage submitRegistration() {
    signUpBtn.click();
    return this;
  }

  @Step("Register with credentials: username: '{0}', password: '{1}'")
  @Nonnull
  public SuccessfulRegistrationPage doRegister(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    passwordSubmitInput.setValue(password);
    signUpBtn.click();
    return new SuccessfulRegistrationPage();
  }

  @Step("Check passwords")
  public void checkThatPasswordsAreNotEquals() {
    passwordErrorText.shouldHave(text("Passwords should be equal"));
  }

  @Step("Check username")
  public void checkThatUsernameLengthIsIncorrect() {
    usernameErrorText.shouldHave(text("Allowed username length should be from 3 to 50 characters"));
  }

  @Step("Check password length")
  public void checkThatPasswordLengthIsIncorrect() {
    passwordErrorText.shouldHave(text("Allowed password length should be from 3 to 12 characters"));
    passwordSubmitErrorText.shouldHave(text("Allowed password length should be from 3 to 12 characters"));
  }

  @Step("Check username filled")
  public void checkThatUsernameIsFilled() {
    usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
  }

  @Step("Check password filled")
  public void checkThatPasswordIsFilled() {
    passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
  }

  @Step("Check passwords filled")
  public void checkThatPasswordSubmitIsFilled() {
    passwordSubmitInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
  }

  @Step("Check username")
  public void checkThatUsernameIsAlreadyExist(String username) {
    usernameErrorText.shouldHave(text("Username `" + username + "` already exists"));
  }
}
