package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
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
  @ApiLogin
  @Test
  void archiveCategory(UserJson user) {
    String categoryName = user.testData().categories().getFirst().name();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .categoriesShouldHaveLabel(categoryName)
        .archiveCategory(categoryName)
        .checkArchivedCategoryExists(categoryName);
  }

  @User(
      categories = @Category(
          archived = true
      )
  )
  @ApiLogin
  @Test
  void unArchiveCategory(UserJson user) {
    String categoryName = user.testData().categories().getFirst().name();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .showArchivedCategories()
        .categoriesShouldHaveLabel(categoryName)
        .unArchiveCategory(categoryName)
        .checkCategoryExists(categoryName);
  }

  @User
  @ApiLogin
  @ScreenShotTest(value = "img/expected-avatar.png")
  void checkProfileImageTest(UserJson user, BufferedImage expectedProfileImage) throws IOException {
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .uploadAvatarFromClasspath("img/avatar.png")
        .checkAvatarPicture(expectedProfileImage);
  }

  @User
  @ApiLogin
  @Test
  void editProfile(UserJson user) {
    String editName = RandomDataUtils.randomName();
    ProfilePage profilePage =
        Selenide.open(ProfilePage.URL, ProfilePage.class)
            .changeName(editName)
            .checkAlertMessage("Profile successfully updated");

    Selenide.refresh();
    profilePage.checkName(editName);
  }
}
