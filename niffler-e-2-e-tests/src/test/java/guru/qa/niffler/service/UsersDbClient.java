package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;
import static guru.qa.niffler.utils.RandomDataUtils.randomUsername;

public class UsersDbClient {


  private static final Config CFG = Config.getInstance();
  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
  private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositoryHibernate();

  private final AuthUserDao authUserDaoSpringJdbc = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDaoSpringJdbc = new AuthAuthorityDaoSpringJdbc();
  private final UdUserDao udUserDaoSpringJdbc = new UdUserDaoSpringJdbc();

  private final AuthUserDao authUserDaoJdbc = new AuthUserDaoJdbc();
  private final AuthAuthorityDao authAuthorityDaoJdbc = new AuthAuthorityDaoJdbc();
  private final UdUserDao udUserDaoJdbc = new UdUserDaoJdbc();

  private final TransactionTemplate chainedTxTemplate = new TransactionTemplate(
      new ChainedTransactionManager(
          new JdbcTransactionManager(
              dataSource(CFG.authJdbcUrl())
          ),
          new JdbcTransactionManager(
              dataSource(CFG.userdataJdbcUrl())
          )
      )
  );

  private final TransactionTemplate txTemplate = new TransactionTemplate(
      new JdbcTransactionManager(
          dataSource(CFG.authJdbcUrl())
      )
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(),
      CFG.userdataJdbcUrl()
  );

  //Hibernate с транзакциями
  public UserJson txCreateUserHibernate(String username, String password) {
    return xaTransactionTemplate.execute(() -> {
          AuthUserEntity authUser = authUserEntity(username, password);
          authUserRepository.create(authUser);
          return UserJson.fromEntity(
              userdataUserRepository.create(userEntity(username)),
              null
          );
        }
    );
  }

  public void addIncomeInvitation(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
              String username = randomUsername();
              AuthUserEntity authUser = authUserEntity(username, "12345");
              authUserRepository.create(authUser);
              UserEntity adressee = userdataUserRepository.create(userEntity(username));
              userdataUserRepository.addIncomeInvitation(targetEntity, adressee);
              return null;
            }
        );
      }
    }
  }

  public void addOutcomeInvitation(UserJson targetUser, int count) {
    if (count > 0) {
      UserEntity targetEntity = userdataUserRepository.findById(
          targetUser.id()
      ).orElseThrow();

      for (int i = 0; i < count; i++) {
        xaTransactionTemplate.execute(() -> {
              String username = randomUsername();
              AuthUserEntity authUser = authUserEntity(username, "12345");
              authUserRepository.create(authUser);
              UserEntity adressee = userdataUserRepository.create(userEntity(username));
              userdataUserRepository.addOutcomeInvitation(targetEntity, adressee);
              return null;
            }
        );
      }
    }
  }

  void addFriend(UserJson targetUser, int count) {

  }

  //Spring JDBC с транзакциями
  public UserJson txCreateUserSpringJdbc(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
          AuthUserEntity authUser = new AuthUserEntity();
          authUser.setUsername(user.username());
          authUser.setPassword(pe.encode("12345"));
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
          authUserRepository.create(authUser);
          return UserJson.fromEntity(
              udUserDaoSpringJdbc.create(UserEntity.fromJson(user)),
              null
          );
        }
    );
  }

  //Spring JDBC без транзакций
  public UserJson createUserSpringJdbc(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);

    AuthUserEntity createdAuthUser = authUserDaoSpringJdbc.create(authUser);

    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
        e -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setUser(createdAuthUser);
          ae.setAuthority(e);
          return ae;
        }
    ).toArray(AuthorityEntity[]::new);

    authAuthorityDaoSpringJdbc.create(authorityEntities);
    return UserJson.fromEntity(
        udUserDaoSpringJdbc.create(UserEntity.fromJson(user)),
        null
    );
  }

  //JDBC с транзакциями
  public UserJson txCreateUserJdbc(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
          AuthUserEntity authUser = new AuthUserEntity();
          authUser.setUsername(null);
          authUser.setPassword(pe.encode("12345"));
          authUser.setEnabled(true);
          authUser.setAccountNonExpired(true);
          authUser.setAccountNonLocked(true);
          authUser.setCredentialsNonExpired(true);

          AuthUserEntity createdAuthUser = authUserDaoJdbc.create(authUser);

          AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
              e -> {
                AuthorityEntity ae = new AuthorityEntity();
                ae.setUser(createdAuthUser);
                ae.setAuthority(e);
                return ae;
              }
          ).toArray(AuthorityEntity[]::new);

          authAuthorityDaoJdbc.create(authorityEntities);
          return UserJson.fromEntity(
              udUserDaoJdbc.create(UserEntity.fromJson(user)),
              null
          );
        }
    );
  }

  //JDBC без транзакций
  public UserJson createUserJdbc(UserJson user) {
    AuthUserEntity authUser = new AuthUserEntity();
    authUser.setUsername(user.username());
    authUser.setPassword(pe.encode("12345"));
    authUser.setEnabled(true);
    authUser.setAccountNonExpired(true);
    authUser.setAccountNonLocked(true);
    authUser.setCredentialsNonExpired(true);

    AuthUserEntity createdAuthUser = authUserDaoJdbc.create(authUser);

    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
        e -> {
          AuthorityEntity ae = new AuthorityEntity();
          ae.setUser(createdAuthUser);
          ae.setAuthority(e);
          return ae;
        }
    ).toArray(AuthorityEntity[]::new);

    authAuthorityDaoJdbc.create(authorityEntities);
    return UserJson.fromEntity(
        udUserDaoJdbc.create(UserEntity.fromJson(user)),
        null
    );
  }

  //Spring JDBC c ChainedTransactionManager
  public UserJson chainedTxCreateUserSpringJdbc(UserJson user) {
    return chainedTxTemplate.execute(status -> {
      AuthUserEntity authUserEntity = new AuthUserEntity();
      authUserEntity.setUsername(user.username());
      authUserEntity.setPassword(pe.encode("12345"));
      authUserEntity.setEnabled(true);
      authUserEntity.setAccountNonExpired(true);
      authUserEntity.setAccountNonLocked(true);
      authUserEntity.setCredentialsNonExpired(true);

      AuthUserEntity createdAuthUser = authUserDaoSpringJdbc.create(authUserEntity);
      AuthorityEntity[] userAuthorities = Arrays.stream(Authority.values()).map(
          e -> {
            AuthorityEntity ae = new AuthorityEntity();
            ae.setUser(createdAuthUser);
            ae.setAuthority(e);
            return ae;
          }).toArray(AuthorityEntity[]::new);

      authAuthorityDaoSpringJdbc.create(userAuthorities);
      return UserJson.fromEntity(
          udUserDaoSpringJdbc.create(UserEntity.fromJson(user)),
          null
      );
    });
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