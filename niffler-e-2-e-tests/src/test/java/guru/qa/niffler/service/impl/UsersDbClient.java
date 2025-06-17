package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.FriendshipStatus;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;
import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepository = AuthUserRepository.getInstance();
  private final UserdataUserRepository userdataUserRepository = UserdataUserRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  @Step("Create user with username '{0}' using SQL INSERT")
  @Nonnull
  @Override
  public UserJson createUser(String username, String password) {
    return requireNonNull(xaTransactionTemplate.execute(() -> {
          authUserRepository.create(
              authUserEntity(username, password)
          );
          return UserJson.fromEntity(
              userdataUserRepository.create(
                  userEntity(username)
              ),
              null
          ).withTestData(new TestData(password));
        }
    ));
  }

  @Step("Create {1} income invitations for user using SQL INSERT")
  @Override
  public void createIncomeInvitations(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        targetUser.testData()
            .incomeInvitations()
            .add(UserJson.fromEntity(
                    requireNonNull(
                        xaTransactionTemplate.execute(() -> {
                              final String username = randomUsername();
                              final UserEntity newUser = createNewUser(username, "12345");
                              userdataUserRepository.sendInvitation(
                                  newUser,
                                  targetEntity
                              );
                              return newUser;
                            }
                        )),
                    FriendshipStatus.INVITE_RECEIVED
                )
            );
      }
    }
  }

  @Step("Create {1} outcome invitations for user using SQL INSERT")
  @Override
  public void createOutcomeInvitations(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        targetUser.testData()
            .outcomeInvitations()
            .add(UserJson.fromEntity(
                    requireNonNull(
                        xaTransactionTemplate.execute(() -> {
                              final String username = randomUsername();
                              final UserEntity newUser = createNewUser(username, "12345");
                              userdataUserRepository.sendInvitation(
                                  targetEntity,
                                  newUser
                              );
                              return newUser;
                            }
                        )),
                    FriendshipStatus.INVITE_SENT
                )
            );
      }
    }
  }

  @Step("Create {1} friends for user using SQL INSERT")
  @Override
  public void createFriends(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        targetUser.testData()
            .friends()
            .add(UserJson.fromEntity(
                    requireNonNull(
                        xaTransactionTemplate.execute(() -> {
                              final String username = randomUsername();
                              final UserEntity newUser = createNewUser(username, "12345");
                              userdataUserRepository.addFriend(
                                  targetEntity,
                                  newUser
                              );
                              return newUser;
                            }
                        )),
                    FriendshipStatus.FRIEND
                )
            );
      }
    }
  }

  private UserEntity createNewUser(String username, String password) {
    AuthUserEntity authUser = authUserEntity(username, password);
    authUserRepository.create(authUser);
    return userdataUserRepository.create(userEntity(username));
  }


  private UserEntity userEntity(String username) {
    UserEntity ue = new UserEntity();
    ue.setUsername(username);
    ue.setCurrency(CurrencyValues.RUB);
    return ue;
  }

  private AuthUserEntity authUserEntity(String username, String password) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(username);
    authUser.setPassword(pe.encode(password));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);
    authUser.setAuthorities(
        Arrays.stream(Authority.values()).map(
            e -> {
              AuthorityEntity ae = new AuthorityEntity();
              ae.setUser(authUser);
              ae.setAuthority(e);
              return ae;
            }
        ).toList()
    );
    return authUser;
  }
}