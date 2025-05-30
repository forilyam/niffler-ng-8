package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class AuthUserDaoJdbc implements AuthUserDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.authJdbcUrl();

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setBoolean(3, user.getEnabled());
      ps.setBoolean(4, user.getAccountNonExpired());
      ps.setBoolean(5, user.getAccountNonLocked());
      ps.setBoolean(6, user.getCredentialsNonExpired());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      user.setId(generatedKey);
      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public AuthUserEntity update(AuthUserEntity user) {
    String updateUserSql = "UPDATE \"user\" SET password = ?, enabled = ?, " +
        "account_non_expired = ?, account_non_locked = ?, credentials_non_expired = ? " +
        "WHERE id = ?";
    try (PreparedStatement updateUserPs = holder(url).connection().prepareStatement(updateUserSql);) {

      updateUserPs.setString(1, user.getPassword());
      updateUserPs.setBoolean(2, user.getEnabled());
      updateUserPs.setBoolean(3, user.getAccountNonExpired());
      updateUserPs.setBoolean(4, user.getAccountNonLocked());
      updateUserPs.setBoolean(5, user.getCredentialsNonExpired());
      updateUserPs.setObject(6, user.getId());
      updateUserPs.executeUpdate();

      return user;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @Override
  public void updateUserAuthority(AuthUserEntity user) {
    String clearAuthoritySql = "DELETE FROM \"authority\" WHERE user_id = ?";
    String insertAuthoritySql = "INSERT INTO \"authority\" (user_id, authority) VALUES (?, ?)";
    try (PreparedStatement clearAuthorityPs = holder(url).connection().prepareStatement(clearAuthoritySql);
         PreparedStatement authorityPs = holder(url).connection().prepareStatement(insertAuthoritySql)) {
      clearAuthorityPs.setObject(1, user.getId());
      clearAuthorityPs.executeUpdate();

      for (AuthorityEntity authority : user.getAuthorities()) {
        authorityPs.setObject(1, user.getId());
        authorityPs.setString(2, authority.getAuthority().name());
        authorityPs.addBatch();
      }
      authorityPs.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public Optional<AuthUserEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM \"user\" WHERE id = ?"
    )) {
      ps.setObject(1, id);

      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          return Optional.ofNullable(
              AuthUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM \"user\" WHERE username = ?"
    )) {
      ps.setString(1, username);

      ps.execute();

      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          return Optional.ofNullable(
              AuthUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public List<AuthUserEntity> findAll() {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM \"user\""
    )) {
      ps.execute();
      List<AuthUserEntity> result = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          result.add(
              AuthUserEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        }
      }
      return result;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @Override
  public void delete(AuthUserEntity user) {
    try (PreparedStatement userPs = holder(url).connection().prepareStatement(
        "DELETE FROM \"user\" WHERE id = ?");
         PreparedStatement authorityPS = holder(url).connection().prepareStatement(
             "DELETE FROM \"authority\" WHERE user_id = ?")
    ) {
      userPs.setObject(1, user.getId());
      userPs.executeUpdate();

      authorityPS.setObject(1, user.getId());
      authorityPS.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
