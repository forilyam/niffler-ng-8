package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.impl.hibernate.UserdataUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.UserdataUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.UserdataUserRepositorySpringJdbc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface UserdataUserRepository {

  @Nonnull
  static UserdataUserRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new UserdataUserRepositoryHibernate();
      case "jdbc" -> new UserdataUserRepositoryJdbc();
      case "sjdbc" -> new UserdataUserRepositorySpringJdbc();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  UserEntity create(UserEntity user);

  @Nonnull
  Optional<UserEntity> findById(UUID id);

  @Nonnull
  Optional<UserEntity> findByUsername(String username);

  @Nonnull
  UserEntity update(UserEntity user);

  void sendInvitation(UserEntity requester, UserEntity addressee);

  void addFriend(UserEntity requester, UserEntity addressee);

  void remove(UserEntity user);
}
