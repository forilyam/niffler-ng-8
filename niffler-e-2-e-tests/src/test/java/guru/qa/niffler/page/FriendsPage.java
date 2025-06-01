package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.textsInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public class FriendsPage {
  private final SelenideElement peopleTab = $("a[href='/people/friends']");
  private final SelenideElement allTab = $("a[href='/people/all']");
  private final SelenideElement friendsTable = $("#friends");
  private final SelenideElement friendsTab = $("#simple-tabpanel-friends");
  private final SelenideElement requestsTable = $("#requests");
  private final SelenideElement searchInput = $("input");
  private final SelenideElement dialogWindow = $("div[role='dialog']");

  @Step("Check that the friends page is loaded")
  @Nonnull
  public FriendsPage checkThatFriendsPageLoaded() {
    peopleTab.shouldBe(visible);
    allTab.shouldBe(visible);
    return this;
  }

  @Step("Check that friends list contains names: '{0}'")
  public void checkExistingFriends(String... existingFriends) {
    for (String friend : existingFriends) {
      searchInput.setValue(friend).pressEnter();
      friendsTable.$$("tr").shouldHave(textsInAnyOrder(existingFriends));
    }
  }

  @Step("Check that friends list is empty")
  public void checkFriendsTableIsEmpty() {
    friendsTable.shouldNotBe(visible);
    friendsTab.shouldHave(text("There are no users yet"));
  }

  @Step("Check that income invitation from '{incomeUsername}' is present")
  public void checkIncomeInvitation(String incomeUsername) {
    searchInput.setValue(incomeUsername).pressEnter();
    requestsTable.$$("tr").shouldHave(textsInAnyOrder(incomeUsername));
  }

  @Step("Check that income invitations count is equal to {expectedCount}")
  @Nonnull
  public FriendsPage checkExistingInvitationsCount(int expectedCount) {
    requestsTable.$$("tr").shouldHave(size(expectedCount));
    return this;
  }

  @Step("Accept invitation from user: {username}")
  @Nonnull
  public FriendsPage acceptFriendInvitationFromUser(String username) {
    SelenideElement friendRow = requestsTable.$$("tr").find(text(username));
    friendRow.$(byText("Accept")).click();
    return this;
  }

  @Step("Check that friends count is equal to {expectedCount}")
  @Nonnull
  public FriendsPage checkExistingFriendsCount(int expectedCount) {
    friendsTable.$$("tr").shouldHave(size(expectedCount));
    return this;
  }

  @Step("Decline invitation from user: {username}")
  @Nonnull
  public FriendsPage declineFriendInvitationFromUser(String username) {
    SelenideElement friendRow = requestsTable.$$("tr").find(text(username));
    friendRow.$(byText("Decline")).click();
    dialogWindow.$(byText("Decline")).click(usingJavaScript());
    return this;
  }
}
