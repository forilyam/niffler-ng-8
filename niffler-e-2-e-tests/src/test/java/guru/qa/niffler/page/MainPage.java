package guru.qa.niffler.page;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement statistics = $("#stat");
  private final SelenideElement spendings = $("#spendings");
  private final SelenideElement profileBtn = $x("//*[@data-testid='PersonIcon']");
  private final SelenideElement profileLink = $("a[href='/profile']");
  private final SelenideElement friendsLink = $("a[href='/people/friends']");
  private final SelenideElement searchInput = $("input");

  private final ElementsCollection stats = $("#stat #legend-container").$$("li");
  private final SelenideElement statisticCanvas = $("canvas[role='img']");
  private final SelenideElement deleteBtn = $("#delete");
  private final SelenideElement dialogWindow = $("div[role='dialog']");

  public EditSpendingPage editSpending(String spendingDescription) {
    searchInput.setValue(spendingDescription).pressEnter();

    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public void checkThatTableContains(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .should(visible);
  }

  public void checkThatMainPageIsLoaded() {
    statistics.shouldBe(visible).shouldHave(text("Statistics"));;
    spendings.shouldBe(visible).shouldHave(text("History of Spendings"));
  }

  public ProfilePage goToProfile() {
    profileBtn.click();
    profileLink.click();
    return new ProfilePage();
  }

  public FriendsPage goToFriends() {
    profileBtn.click();
    friendsLink.click();
    return new FriendsPage();
  }

  public MainPage checkStatisticContains(String... texts) {
    stats.should(CollectionCondition.texts(texts));
    return this;
  }

  public MainPage checkStatisticImage(BufferedImage expectedImage) throws IOException {
    Selenide.sleep(2000);
    BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(statisticCanvas.screenshot()));
    assertFalse(
        new ScreenDiffResult(
            actualImage,
            expectedImage
        )
    );
    return this;
  }

  public MainPage deleteSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(0)
        .click();
    deleteBtn.click();
    dialogWindow.$(byText("Delete")).click();
    return new MainPage();
  }
}
