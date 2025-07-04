package guru.qa.niffler.data.dao;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface AuthUserDao {
  @Nonnull
  AuthUserEntity create(AuthUserEntity user);

  @Nonnull
  AuthUserEntity update(AuthUserEntity user);

  void updateUserAuthority(AuthUserEntity user);

  @Nonnull
  Optional<AuthUserEntity> findById(UUID id);

  @Nonnull
  Optional<AuthUserEntity> findByUsername(String username);

  @Nonnull
  List<AuthUserEntity> findAll();

  void delete(AuthUserEntity user);
}
