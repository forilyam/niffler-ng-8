package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class RegisterPage {
  private final SelenideElement usernameInput = $("input[name='username']");
  private final SelenideElement passwordInput = $("input[name='password']");
  private final SelenideElement passwordSubmitInput = $("input[name='passwordSubmit']");
  private final SelenideElement signUpBtn = $("button[type='submit']");
  private final SelenideElement usernameErrorText = $("#username + .form__error");
  private final SelenideElement passwordErrorText = $x("//*[@id='password']/following-sibling::span");
  private final SelenideElement passwordSubmitErrorText = $x("//*[@id='passwordSubmit']/following-sibling::span");
  private final String VALIDATION_MESSAGE = "Заполните это поле.";

  public RegisterPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public RegisterPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public RegisterPage setPasswordSubmit(String password) {
    passwordSubmitInput.setValue(password);
    return this;
  }

  public RegisterPage submitRegistration() {
    signUpBtn.click();
    return this;
  }

  public SuccessfulRegistrationPage doRegister(String username, String password) {
    usernameInput.setValue(username);
    passwordInput.setValue(password);
    passwordSubmitInput.setValue(password);
    signUpBtn.click();
    return new SuccessfulRegistrationPage();
  }

  public void checkThatPasswordsAreNotEquals() {
    passwordErrorText.shouldHave(text("Passwords should be equal"));
  }

  public void checkThatUsernameLengthIsIncorrect() {
    usernameErrorText.shouldHave(text("Allowed username length should be from 3 to 50 characters"));
  }

  public void checkThatPasswordLengthIsIncorrect() {
    passwordErrorText.shouldHave(text("Allowed password length should be from 3 to 12 characters"));
    passwordSubmitErrorText.shouldHave(text("Allowed password length should be from 3 to 12 characters"));
  }

  public void checkThatUsernameIsFilled() {
    usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
  }

  public void checkThatPasswordIsFilled() {
    passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
  }

  public void checkThatPasswordSubmitIsFilled() {
    passwordSubmitInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
  }

  public void checkThatUsernameIsAlreadyExist(String username) {
    usernameErrorText.shouldHave(text("Username `" + username + "` already exists"));
  }
}
