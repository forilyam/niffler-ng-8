package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

@ParametersAreNonnullByDefault
public class SpendDaoJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.spendJdbcUrl();

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public SpendEntity create(SpendEntity spend) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "INSERT INTO \"spend\" (username, spend_date, currency, amount, description, category_id) " +
            "VALUES ( ?, ?, ?, ?, ?, ?)",
        Statement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Can`t find id in ResultSet");
        }
      }
      spend.setId(generatedKey);
      return spend;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public SpendEntity update(SpendEntity spend) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "UPDATE \"spend\" SET spend_date = ?, currency = ?, amount = ?, description = ? " +
            "WHERE id = ?")
    ) {
      ps.setDate(1, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(2, spend.getCurrency().name());
      ps.setDouble(3, spend.getAmount());
      ps.setString(4, spend.getDescription());
      ps.setObject(5, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return spend;
  }

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM \"spend\" WHERE id = ?"
    )) {
      ps.setObject(1, id);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          return Optional.ofNullable(
              SpendEntityRowMapper.instance.mapRow(rs, rs.getRow())
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
  public List<SpendEntity> findAllByUsername(String username) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM \"spend\" WHERE username = ?"
    )) {
      ps.setString(1, username);
      ps.execute();
      List<SpendEntity> spends = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          spends.add(
              SpendEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        }
      }
      return spends;
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @Override
  public void delete(SpendEntity spend) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "DELETE FROM \"spend\" WHERE id = ?"
    )) {
      ps.setObject(1, spend.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public List<SpendEntity> findAll() {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM \"spend\""
    )) {
      ps.execute();
      List<SpendEntity> spends = new ArrayList<>();
      try (ResultSet rs = ps.getResultSet()) {
        while (rs.next()) {
          spends.add(
              SpendEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        }
        return spends;
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  @SuppressWarnings("resource")
  @NotNull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    try (PreparedStatement ps = holder(url).connection().prepareStatement(
        "SELECT * FROM \"spend\" WHERE username = ? and description = ?"
    )) {
      ps.setString(1, username);
      ps.setString(2, description);
      ps.execute();
      try (ResultSet rs = ps.getResultSet()) {
        if (rs.next()) {
          return Optional.ofNullable(
              SpendEntityRowMapper.instance.mapRow(rs, rs.getRow())
          );
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
}
