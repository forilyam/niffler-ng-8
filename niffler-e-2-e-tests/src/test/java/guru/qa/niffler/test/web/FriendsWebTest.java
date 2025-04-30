package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class FriendsWebTest {

  private static final Config CFG = Config.getInstance();

  @User(friends = 1)
  @Test
  void friendShouldBePresentInFriendsTable(UserJson user) {
    final String friendUsername =
        user.testData().friends().stream()
            .map(UserJson::username)
            .toArray(String[]::new)[0];

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .goToFriends()
        .checkThatFriendsPageLoaded()
        .checkExistingFriends(friendUsername);
  }

  @User
  @Test
  void friendsTableShouldBeEmptyForNewUser(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .goToFriends()
        .checkThatFriendsPageLoaded()
        .checkFriendsTableIsEmpty();
  }

  @User(incomeInvitations = 1)
  @Test
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .goToFriends()
        .checkThatFriendsPageLoaded()
        .checkIncomeInvitation(user.testData().incomeInvitations().getFirst().username());
  }

  @User(outcomeInvitations = 1)
  @Test
  void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .goToFriends()
        .checkThatFriendsPageLoaded()
        .goToAllPeopleTab()
        .checkOutcomeInvitation(user.testData().outcomeInvitations().getFirst().username());
  }
}
