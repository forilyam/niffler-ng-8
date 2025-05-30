package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.userdata.FriendshipEntity;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserdataUserEntityRowMapper;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserdataUserExtractor implements ResultSetExtractor<UserEntity> {

  public static final UserdataUserExtractor instance = new UserdataUserExtractor();

  private UserdataUserExtractor() {
  }

  @Nullable
  @Override
  public UserEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
    UserEntity user = null;
    while (rs.next()) {
      if (user == null) {
        user = UserdataUserEntityRowMapper.instance.mapRow(rs, 0);
      }
      UUID requesterId = rs.getObject("requester_id", UUID.class);
      UUID addresseeId = rs.getObject("addressee_id", UUID.class);
      if (requesterId != null) {
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setRequester(new UserEntity(requesterId));
        friendship.setAddressee(new UserEntity(addresseeId));
        friendship.setStatus(FriendshipStatus.valueOf(rs.getString("friendship_status")));
        friendship.setCreatedDate(rs.getDate("created_date"));

        if (user.getId().equals(requesterId)) {
          if (user.getFriendshipRequests().stream().noneMatch(f ->
              f.getRequester().getId().equals(requesterId) &&
                  f.getAddressee().getId().equals(addresseeId))) {
            user.getFriendshipRequests().add(friendship);
          }
        } else if (user.getId().equals(addresseeId)) {
          if (user.getFriendshipAddressees().stream().noneMatch(f ->
              f.getRequester().getId().equals(requesterId) &&
                  f.getAddressee().getId().equals(addresseeId))) {
            user.getFriendshipAddressees().add(friendship);
          }
        }
      }
    }
    return user;
  }
}
