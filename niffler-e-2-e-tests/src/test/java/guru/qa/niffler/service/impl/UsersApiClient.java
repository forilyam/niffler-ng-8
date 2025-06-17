package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.RestClient.EmtyRestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.INVITE_RECEIVED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.INVITE_SENT;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UsersApiClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final String defaultPassword = "12345";

  private final AuthApi authApi = new EmtyRestClient(CFG.authUrl()).create(AuthApi.class);
  private final UserdataApi userdataApi = new EmtyRestClient(CFG.userdataUrl()).create(UserdataApi.class);

  @Step("Create user with username '{0}' using REST API")
  @Nonnull
  @Override
  public UserJson createUser(String username, String password) {
    try {
      authApi.requestRegisterForm().execute();
      authApi.register(
          username,
          password,
          password,
          ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
      ).execute();
      UserJson createdUser = requireNonNull(userdataApi.currentUser(username).execute().body());
      return createdUser.withPassword(
          defaultPassword
      );
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Step("Create {1} income invitations for user using REST API")
  @Override
  public void createIncomeInvitations(UserJson targetUser, int count) {
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        final String username = randomUsername();
        final Response<UserJson> response;
        final UserJson newUser;
        try {
          newUser = createUser(username, defaultPassword);

          response = userdataApi.sendInvitation(
              newUser.username(),
              targetUser.username()
          ).execute();
        } catch (IOException e) {
          throw new AssertionError(e);
        }
        assertEquals(200, response.code());
      }
    }
  }

  @Step("Create {1} outcome invitations for user using REST API")
  @Override
  public void createOutcomeInvitations(UserJson targetUser, int count) {
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        final String username = randomUsername();
        final Response<UserJson> response;
        final UserJson newUser;
        try {
          newUser = createUser(username, defaultPassword);

          response = userdataApi.sendInvitation(
              targetUser.username(),
              newUser.username()
          ).execute();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
      }
    }
  }

  @Step("Create {1} friends for user using REST API")
  @Override
  public void createFriends(UserJson targetUser, int count) {
    if (count > 0) {
      for (int i = 0; i < count; i++) {
        final String username = randomUsername();
        final Response<UserJson> response;
        final UserJson newUser;
        try {
          newUser = createUser(username, defaultPassword);

          userdataApi.sendInvitation(
              newUser.username(),
              targetUser.username()
          ).execute();
          response = userdataApi.acceptInvitation(targetUser.username(), username).execute();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
      }
    }
  }

  @Step("Get all users using REST API")
  @Nonnull
  public List<UserJson> allUsers(String username, String searchQuery) {
    final Response<List<UserJson>> response;
    try {
      response = userdataApi.allUsers(username, searchQuery).execute();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    assertEquals(200, response.code());
    return response.body() != null ? response.body() : Collections.emptyList();
  }

  @Step("Get current user using API")
  @Nonnull
  public UserJson currentUser(@Nonnull String username) {
    final Response<UserJson> response;
    try {
      response = userdataApi.currentUser(username)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body();
  }

  @Step("Get user's friends using API")
  @Nonnull
  public List<UserJson> getFriends(@Nonnull String username) {
    final Response<List<UserJson>> response;
    try {
      response = userdataApi.friends(username, null)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return response.body() != null ? response.body() : Collections.emptyList();
  }

  @Step("Get user's income invitations using API")
  @Nonnull
  public List<UserJson> getIncomeInvitations(@Nonnull String username) {
    List<UserJson> friends = getFriends(username);

    return friends.stream()
        .filter(userJson -> userJson.friendshipStatus().equals(INVITE_RECEIVED))
        .toList();
  }

  @Step("Get user's outcome invitations using API")
  @Nonnull
  public List<UserJson> getOutcomeInvitations(@Nonnull String username) {
    List<UserJson> allPeople = allUsers(username, null);

    return allPeople.stream()
        .filter(userJson -> userJson.friendshipStatus().equals(INVITE_SENT))
        .toList();
  }
}
