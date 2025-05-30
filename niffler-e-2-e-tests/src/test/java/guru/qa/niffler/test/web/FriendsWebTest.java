package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.FriendsPage;
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
        .getHeader()
        .toFriendsPage()
        .checkThatFriendsPageLoaded()
        .checkExistingFriends(friendUsername);
  }

  @User
  @Test
  void friendsTableShouldBeEmptyForNewUser(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .getHeader()
        .toFriendsPage()
        .checkThatFriendsPageLoaded()
        .checkFriendsTableIsEmpty();
  }

  @User(incomeInvitations = 1)
  @Test
  void incomeInvitationBePresentInFriendsTable(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .getHeader()
        .toFriendsPage()
        .checkThatFriendsPageLoaded()
        .checkIncomeInvitation(user.testData().incomeInvitations().getFirst().username());
  }

  @User(outcomeInvitations = 1)
  @Test
  void outcomeInvitationBePresentInAllPeoplesTable(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password())
        .getHeader()
        .toAllPeoplesPage()
        .checkThatAllPeoplesPageLoaded()
        .checkOutcomeInvitation(user.testData().outcomeInvitations().getFirst().username());
  }

  @User(incomeInvitations = 1)
  @Test
  void shouldAcceptInvitation(UserJson user) {
    final String userToAccept = user.testData().incomeInvitations().getFirst().username();

    FriendsPage friendsPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .checkThatMainPageIsLoaded()
            .getHeader()
            .toFriendsPage()
            .checkExistingInvitationsCount(1)
            .acceptFriendInvitationFromUser(userToAccept)
            .checkExistingInvitationsCount(0);

    Selenide.refresh();
    friendsPage.checkExistingFriendsCount(1)
        .checkExistingFriends(userToAccept);
  }

  @User(incomeInvitations = 1)
  @Test
  void shouldDeclineInvitation(UserJson user) {
    final String userToDecline = user.testData().incomeInvitations().getFirst().username();

    FriendsPage friendsPage =
        Selenide.open(CFG.frontUrl(), LoginPage.class)
            .doLogin(user.username(), user.testData().password())
            .checkThatMainPageIsLoaded()
        .getHeader()
        .toFriendsPage()
        .checkExistingInvitationsCount(1)
        .declineFriendInvitationFromUser(userToDecline)
        .checkExistingInvitationsCount(0);

    Selenide.refresh();
    friendsPage.checkExistingFriendsCount(0);
  }
}
