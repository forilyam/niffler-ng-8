package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

  private final AuthUserDao authUserDao = new AuthUserDaoSpringJdbc();
  private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoSpringJdbc();

  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    authUserDao.create(user);
    authAuthorityDao.create(user.getAuthorities().toArray(new AuthorityEntity[0]));
    return user;
  }

  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    authUserDao.update(user);
    authUserDao.updateUserAuthority(user);
    return user;
  }

  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    Optional<AuthUserEntity> userEntity = authUserDao.findById(id);
    userEntity
        .ifPresent(authUserEntity ->
            authUserEntity.addAuthorities(
                authAuthorityDao.findAllByUserId(authUserEntity.getId()).toArray(new AuthorityEntity[0])));
    return userEntity;
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    Optional<AuthUserEntity> userEntity = authUserDao.findByUsername(username);
    userEntity
        .ifPresent(authUserEntity ->
            authUserEntity.addAuthorities(
                authAuthorityDao.findAllByUserId(authUserEntity.getId()).toArray(new AuthorityEntity[0])));
    return userEntity;
  }

  @Override
  public void remove(AuthUserEntity user) {
    authUserDao.delete(user);
  }
}
