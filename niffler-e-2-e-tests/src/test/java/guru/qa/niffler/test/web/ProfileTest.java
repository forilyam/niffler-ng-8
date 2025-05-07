package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

  private static final Config CFG = Config.getInstance();

  @User(
      categories = @Category(
          archived = false
      )
  )
  @Test
  void archiveCategory(UserJson user) {
    ProfilePage profilePage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .goToProfile();

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
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .goToProfile();

    String categoryName = user.testData().categories().getFirst().name();
    profilePage
        .showArchivedCategories()
        .categoriesShouldHaveLabel(categoryName)
        .unArchiveCategory(categoryName)
        .checkCategoryExists(categoryName);
  }

}
