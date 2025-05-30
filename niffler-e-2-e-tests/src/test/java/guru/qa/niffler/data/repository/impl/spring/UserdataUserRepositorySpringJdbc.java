package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.data.dao.UdUserDao;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;

@ParametersAreNonnullByDefault
public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

  private final UdUserDao udUserDao = new UdUserDaoSpringJdbc();

  @NotNull
  @Override
  public UserEntity create(UserEntity user) {
    return udUserDao.create(user);
  }

  @NotNull
  @Override
  public Optional<UserEntity> findById(UUID id) {
    return udUserDao.findById(id);
  }

  @NotNull
  @Override
  public Optional<UserEntity> findByUsername(String username) {
    return udUserDao.findByUsername(username);
  }

  @NotNull
  @Override
  public UserEntity update(UserEntity user) {
    return udUserDao.update(user);
  }

  @Override
  public void sendInvitation(UserEntity requester, UserEntity addressee) {
    requester.addFriends(FriendshipStatus.PENDING, addressee);
    udUserDao.update(requester);
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    requester.addFriends(ACCEPTED, addressee);
    addressee.addFriends(ACCEPTED, requester);
    udUserDao.update(requester);
    udUserDao.update(addressee);
  }

  @Override
  public void remove(UserEntity user) {
    udUserDao.delete(user);
  }
}
