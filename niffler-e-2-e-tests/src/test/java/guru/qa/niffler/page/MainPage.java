package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.component.StatComponent;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;
import static guru.qa.niffler.condition.SpendConditions.spends;

public class MainPage {

  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement spendings = $("#spendings");
  private final SelenideElement profileBtn = $x("//*[@data-testid='PersonIcon']");
  private final SelenideElement profileLink = $("a[href='/profile']");
  private final SelenideElement friendsLink = $("a[href='/people/friends']");
  private final SelenideElement searchInput = $("input");

  private final ElementsCollection stats = $("#stat #legend-container").$$("li");
  private final SelenideElement statisticCanvas = $("canvas[role='img']");
  private final SelenideElement deleteBtn = $("#delete");
  private final SelenideElement dialogWindow = $("div[role='dialog']");

  private final StatComponent statComponent = new StatComponent();

  public StatComponent getStatComponent() {
    return statComponent;
  }

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
    statComponent.self.shouldBe(visible).shouldHave(text("Statistics"));
    ;
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

  public MainPage deleteSpending(String spendingDescription) {
    tableRows.find(text(spendingDescription))
        .$$("td")
        .get(0)
        .click();
    deleteBtn.click();
    dialogWindow.$(byText("Delete")).click();
    return new MainPage();
  }

  public MainPage checkSpendTable(SpendJson... expectedSpends) {
    tableRows.shouldHave(spends(expectedSpends));
    return this;
  }
}
