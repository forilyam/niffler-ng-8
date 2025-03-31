package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

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

  public RegisterPage doRegister() {
    registerBtn.click();
    return new RegisterPage();
  }

  public LoginPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  public LoginPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  public LoginPage submitLogin() {
    submitBtn.click();
    return this;
  }

  public void checkThatUsernameIsFilled() {
    usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
  }

  public void checkThatPasswordIsFilled() {
    passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));

  }

  public void checkThatUserIsNotCorrect() {
    userIncorrectText.shouldHave(text("Неверные учетные данные пользователя"));
  }

}
