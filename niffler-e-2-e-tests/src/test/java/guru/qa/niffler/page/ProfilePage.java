package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.collections.AnyMatch;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ParametersAreNonnullByDefault
public class ProfilePage extends BasePage<ProfilePage> {

  public static final String URL = CFG.frontUrl() + "profile";

  private final ElementsCollection categoryLabels = $$(".MuiChip-label");
  private final SelenideElement archiveCategoryBtn = $("button[aria-label='Archive category']");
  private final SelenideElement archiveCategoryDialog = $("div[role='dialog']");
  private final SelenideElement dialogDescription = $("#alert-dialog-slide-description");
  private final SelenideElement dialogArchiveBtn = $x("//button[.='Archive']");
  private final SelenideElement dialogUnArchiveBtn = $x("//button[.='Unarchive']");
  private final SelenideElement archiveCategoryAlert = $("div[role='alert']");

  private final SelenideElement archivedSwitcher = $(".MuiSwitch-input");
  private final ElementsCollection bubbles = $$(".MuiChip-filled.MuiChip-colorPrimary");
  private final ElementsCollection bubblesArchived = $$(".MuiChip-filled.MuiChip-colorDefault");

  private final SelenideElement pictureInput = $("input[type='file']");
  private final SelenideElement avatar = $("#image__input").parent().$("img");

  private final SelenideElement userName = $("#username");
  private final SelenideElement nameInput = $("input[name='name']");
  private final SelenideElement saveChangesBtn = $("button[type='submit']");

  @Step("Check that page is loaded")
  @Override
  @Nonnull
  public ProfilePage checkThatPageLoaded() {
    userName.should(visible);
    return this;
  }

  @Step("Check category '{0}' is presented")
  @Nonnull
  public ProfilePage categoriesShouldHaveLabel(String categoryName) {
    categoryLabels.shouldHave(new AnyMatch(
        "Category with name %s is presented".formatted(categoryName),
        category -> category.getText().equals(categoryName)
    ));
    return this;
  }

  @Step("Archive category: '{0}'")
  @Nonnull
  public ProfilePage archiveCategory(String category) {
    archiveCategoryBtn.click();
    archiveCategoryDialog.should(visible);
    dialogDescription.shouldHave(text("Do you really want to archive " + category + "? After this change it won't be available while creating spends"));
    dialogArchiveBtn.click();
    archiveCategoryAlert.should(visible);
    archiveCategoryAlert.shouldHave(text("Category " + category + " is archived"));
    return this;
  }

  @Step("Show archived categories")
  @Nonnull
  public ProfilePage showArchivedCategories() {
    archivedSwitcher.click();
    return this;
  }

  @Step("Unarchive category: '{0}'")
  @Nonnull
  public ProfilePage unArchiveCategory(String category) {
    bubblesArchived.find(text(category)).parent().$("button[aria-label='Unarchive category']").click();
    archiveCategoryDialog.should(visible);
    dialogDescription.shouldHave(text("Do you really want to unarchive category " + category + "?"));
    dialogUnArchiveBtn.click();
    archiveCategoryAlert.should(visible);
    archiveCategoryAlert.shouldHave(text("Category " + category + " is unarchived"));
    return this;
  }

  @Step("Check category: '{0}'")
  public void checkCategoryExists(String category) {
    bubbles.find(text(category)).shouldBe(visible);
  }

  @Step("Check archived category: '{0}'")
  public void checkArchivedCategoryExists(String category) {
    archivedSwitcher.click();
    bubblesArchived.find(text(category)).shouldBe(visible);
  }

  @Step("Upload avatar")
  @Nonnull
  public ProfilePage uploadAvatarFromClasspath(String path) {
    pictureInput.uploadFromClasspath(path);
    return this;
  }

  @Step("Check avatar")
  public void checkAvatarPicture(BufferedImage expected) throws IOException {
    Selenide.sleep(1000);
    assertFalse(
        new ScreenDiffResult(
            avatarScreenshot(), expected
        )
    );
  }

  @Nonnull
  public BufferedImage avatarScreenshot() throws IOException {
    return ImageIO.read(requireNonNull(avatar.screenshot()));
  }


  @Step("Change name: '{0}'")
  @Nonnull
  public ProfilePage changeName(String name) {
    nameInput.clear();
    nameInput.setValue(name).pressEnter();
    saveChangesBtn.click();
    return this;
  }

  @Step("Check name: '{0}'")
  @Nonnull
  public ProfilePage checkName(String name) {
    nameInput.shouldHave(value(name));
    return this;
  }
}
