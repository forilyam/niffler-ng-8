package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class FriendsPage {
  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement friendsTable = $("#friends");
  private final SelenideElement friendsTab = $("#simple-tabpanel-friends");
  private final SelenideElement requestsTable = $("#requests");
  private final SelenideElement allTable = $("#all");

  public FriendsPage checkThatFriendsPageLoaded() {
    peopleTab.shouldBe(visible);
    allTab.shouldBe(visible);
    return this;
  }

  public void checkExistingFriends(String... existingFriends) {
    friendsTable.$$("tr").shouldHave(textsInAnyOrder(existingFriends));
  }

  public void checkFriendsTableIsEmpty() {
    friendsTable.shouldNotBe(visible);
    friendsTab.shouldHave(text("There are no users yet"));
  }

  public void checkIncomeInvitation(String incomeUsername) {
    requestsTable.$$("tr").shouldHave(textsInAnyOrder(incomeUsername));
  }

  public FriendsPage goToAllPeopleTab() {
    allTab.click();
    allTable.shouldBe(visible);
    return this;
  }

  public void checkOutcomeInvitation(String outcomeUsername) {
    allTable.$("tr").shouldHave(text(outcomeUsername));
    allTable.$$("tr").find(text(outcomeUsername)).shouldHave(text("Waiting..."));
  }
}
