package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;

@WebTest
public class LoginTest {

  @Test
  void mainPageShouldBeDisplayedAfterSuccessLogin() {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .doLogin("Ilya", "12345")
        .checkThatPageLoaded();
  }

  @Test
  void shouldShowMessageIfUsernameIsNotFilled() {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .setPassword("a")
        .submitLogin()
        .checkThatUsernameIsFilled();
  }

  @Test
  void shouldShowMessageIfPasswordIsNotFilled() {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .setUsername("a")
        .submitLogin()
        .checkThatPasswordIsFilled();
  }

  @Test
  void userShouldStayOnLoginPageAfterLoginWithBadCredentials() {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .setUsername("a")
        .setPassword("b")
        .submitLogin()
        .checkThatUserIsNotCorrect();
  }

}
