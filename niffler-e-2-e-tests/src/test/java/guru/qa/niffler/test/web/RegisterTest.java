package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.RegisterPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static guru.qa.niffler.utils.RandomDataUtils.randomSentence;

@ExtendWith(BrowserExtension.class)
public class RegisterTest {

  private static final Config CFG = Config.getInstance();

  @Test
  void shouldRegisterNewUser() {
    String newUser = randomUsername();
    String password = "12345";

    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .doRegister(newUser, password)
        .checkMessageOfSuccessfulRegistration("Congratulations! You've registered!")
        .signIn()
        .doLogin(newUser, password)
        .checkThatMainPageIsLoaded();
  }

  @Test
  void shouldShowErrorIfPasswordAndConfirmPasswordAreNotEqual() {
    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setUsername(randomUsername())
        .setPassword("12345")
        .setPasswordSubmit("123456")
        .submitRegistration()
        .checkThatPasswordsAreNotEquals();
  }

  @Test
  void shouldShowErrorIfUsernameMinimalLength() {
    String password = "12345";

    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setUsername("a")
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkThatUsernameLengthIsIncorrect();
  }

  @Test
  void shouldShowErrorIfUsernameMaximumLength() {
    String password = "12345";

    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setUsername(randomSentence(51))
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkThatUsernameLengthIsIncorrect();
  }

  @Test
  void shouldShowErrorIfPasswordMinimalLength() {
    String password = "1";

    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setUsername(randomUsername())
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkThatPasswordLengthIsIncorrect();
  }

  @Test
  void shouldShowErrorIfPasswordMaximumLength() {
    String password = randomSentence(13);

    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setUsername(randomUsername())
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkThatPasswordLengthIsIncorrect();
  }

  @Test
  void shouldShowMessageIfUsernameIsNotFilled() {
    String password = "12345";

    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkThatUsernameIsFilled();
  }

  @Test
  void shouldShowMessageIfPasswordIsNotFilled() {
    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setUsername(randomUsername())
        .submitRegistration()
        .checkThatPasswordIsFilled();
  }

  @Test
  void shouldShowMessageIfPasswordSubmitIsNotFilled() {
    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setUsername(randomUsername())
        .setPassword("12345")
        .submitRegistration()
        .checkThatPasswordSubmitIsFilled();
  }

  @Test
  void shouldNotRegisterUserWithExistingUsername() {
    String existingUsername = "oldUser";
    String password = "12345";

    RegisterPage registerPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doRegister();

    registerPage
        .setUsername(existingUsername)
        .setPassword(password)
        .setPasswordSubmit(password)
        .submitRegistration()
        .checkThatUsernameIsAlreadyExist(existingUsername);
  }

}
