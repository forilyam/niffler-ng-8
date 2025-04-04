package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class ProfileTest {

  private static final Config CFG = Config.getInstance();

  @User(
      username = "Ilya",
      categories = @Category(
          archived = false
      )
  )
  @Test
  void archiveCategory(CategoryJson category) {
    ProfilePage profilePage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin("Ilya", "12345")
            .goToProfile();

    profilePage
        .categoriesShouldHaveLabel(category.name())
        .archiveCategory(category.name())
        .checkArchivedCategoryExists(category.name());
  }

  @User(
      username = "Ilya",
      categories = @Category(
          archived = true
      )
  )
  @Test
  void unArchiveCategory(CategoryJson category) {
    ProfilePage profilePage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin("Ilya", "12345")
            .goToProfile();

    profilePage
        .showArchivedCategories()
        .categoriesShouldHaveLabel(category.name())
        .unArchiveCategory(category.name())
        .checkCategoryExists(category.name());
  }

}
