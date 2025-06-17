package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.impl.hibernate.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.AuthUserRepositorySpringJdbc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserRepository {

  @Nonnull
  static AuthUserRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new AuthUserRepositoryHibernate();
      case "jdbc" -> new AuthUserRepositoryJdbc();
      case "sjdbc" -> new AuthUserRepositorySpringJdbc();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  AuthUserEntity create(AuthUserEntity user);

  @Nonnull
  AuthUserEntity update(AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(UUID id);

  @Nonnull
  Optional<AuthUserEntity> findByUsername(String username);

  void remove(AuthUserEntity user);

}