package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.awt.image.BufferedImage;
import java.io.IOException;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

  @User(
      categories = @Category(
          archived = false
      )
  )
  @Test
  void archiveCategory(UserJson user) {
    ProfilePage profilePage =
        Selenide.open(LoginPage.URL, LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .getHeader()
            .toProfilePage();

    String categoryName = user.testData().categories().getFirst().name();
    profilePage
        .categoriesShouldHaveLabel(categoryName)
        .archiveCategory(categoryName)
        .checkArchivedCategoryExists(categoryName);
  }

  @User(
      categories = @Category(
          archived = true
      )
  )
  @Test
  void unArchiveCategory(UserJson user) {
    ProfilePage profilePage =
        Selenide.open(LoginPage.URL, LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .getHeader()
            .toProfilePage();

    String categoryName = user.testData().categories().getFirst().name();
    profilePage
        .showArchivedCategories()
        .categoriesShouldHaveLabel(categoryName)
        .unArchiveCategory(categoryName)
        .checkCategoryExists(categoryName);
  }

  @User
  @ScreenShotTest(value = "img/expected-avatar.png")
  void checkProfileImageTest(UserJson user, BufferedImage expectedProfileImage) throws IOException {
    Selenide.open(LoginPage.URL, LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .getHeader()
        .toProfilePage()
        .uploadAvatarFromClasspath("img/avatar.png")
        .checkAvatarPicture(expectedProfileImage);
  }

  @User
  @Test
  void editProfile(UserJson user) {
    String editName = RandomDataUtils.randomName();
    ProfilePage profilePage =
        Selenide.open(LoginPage.URL, LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .checkThatPageLoaded()
            .getHeader()
            .toProfilePage()
            .changeName(editName)
            .checkAlertMessage("Profile successfully updated");

    Selenide.refresh();
    profilePage.checkName(editName);
  }
}
