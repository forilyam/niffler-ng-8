package guru.qa.niffler.data.repository.impl.spring;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.extractor.UserdataUserExtractor;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.ACCEPTED;
import static guru.qa.niffler.data.entity.userdata.FriendshipStatus.PENDING;

public class UserdataUserRepositorySpringJdbc implements UserdataUserRepository {

  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity create(UserEntity user) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(
        con -> {
          PreparedStatement ps = con.prepareStatement(
              "INSERT INTO \"user\" (username, currency, firstname, surname, full_name, photo, photo_small) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ? )",
              PreparedStatement.RETURN_GENERATED_KEYS);
          ps.setString(1, user.getUsername());
          ps.setString(2, user.getCurrency().name());
          ps.setString(3, user.getFirstname());
          ps.setString(4, user.getSurname());
          ps.setString(5, user.getFullname());
          ps.setBytes(6, user.getPhoto());
          ps.setBytes(7, user.getPhotoSmall());
          return ps;
        }, kh
    );
    final UUID generatedKey = (UUID) Objects.requireNonNull(kh.getKeys()).get("id");
    user.setId(generatedKey);
    return user;
  }

  @Override
  public Optional<UserEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    return Optional.ofNullable(
        jdbcTemplate.query(
            "SELECT DISTINCT " +
                " u.*," +
                " f.requester_id AS requester_id," +
                " f.addressee_id AS addressee_id," +
                " f.status AS friendship_status," +
                " f.created_date AS created_date" +
                " FROM \"user\" u LEFT JOIN \"friendship\" " +
                " ON u.id = f.requester_id OR u.id = f.addressee_id " +
                " WHERE  u.id = ?",
            UserdataUserExtractor.instance,
            id
        )
    );
  }

  @Override
  public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    jdbcTemplate.update(
        con -> {
          PreparedStatement ps = con.prepareStatement(
              "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                  "VALUES (?, ?, ?, ?)");
          ps.setObject(1, requester.getId());
          ps.setObject(2, addressee.getId());
          ps.setString(3, PENDING.name());
          ps.setObject(4, new Date());
          ps.executeUpdate();
          return ps;
        }
    );
  }

  @Override
  public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    jdbcTemplate.update(
        con -> {
          PreparedStatement ps = con.prepareStatement(
              "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                  "VALUES (?, ?, ?, ?)");
          ps.setObject(1, addressee.getId());
          ps.setObject(2, requester.getId());
          ps.setString(3, PENDING.name());
          ps.setObject(4, new Date());
          ps.executeUpdate();
          return ps;
        }
    );
  }

  @Override
  public void addFriend(UserEntity requester, UserEntity addressee) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
    jdbcTemplate.update(
        con -> {
          PreparedStatement ps = con.prepareStatement(
              "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                  "VALUES (?, ?, ?, ?)");
          ps.setObject(1, requester.getId());
          ps.setObject(2, addressee.getId());
          ps.setString(3, ACCEPTED.name());
          ps.setObject(4, new Date());
          ps.addBatch();

          ps.setObject(1, addressee.getId());
          ps.setObject(2, requester.getId());
          ps.setString(3, ACCEPTED.name());
          ps.setObject(4, new Date());
          ps.addBatch();
          ps.executeBatch();
          return ps;
        }
    );
  }
}
