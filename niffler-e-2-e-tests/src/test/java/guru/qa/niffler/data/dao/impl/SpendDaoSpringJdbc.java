package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.jdbc.DataSources;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendDaoSpringJdbc implements SpendDao {

  private static final Config CFG = Config.getInstance();
  private final String url = CFG.spendJdbcUrl();

  @NotNull
  @Override
  public SpendEntity create(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          """
                  INSERT INTO spend (username, spend_date, currency, amount, description, category_id)
                  VALUES ( ?, ?, ?, ?, ?, ?)
              """,
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, spend.getUsername());
      ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(3, spend.getCurrency().name());
      ps.setDouble(4, spend.getAmount());
      ps.setString(5, spend.getDescription());
      ps.setObject(6, spend.getCategory().getId());
      return ps;
    }, kh);

    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    spend.setId(generatedKey);
    return spend;
  }

  @NotNull
  @Override
  public SpendEntity update(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update(
        """
                UPDATE "spend"
                  SET spend_date =  ?,
                      currency =    ?,
                      amount =      ?,
                      description = ?,
                  WHERE id = ?
            """,
        new java.sql.Date(spend.getSpendDate().getTime()),
        spend.getCurrency().name(),
        spend.getAmount(),
        spend.getDescription(),
        spend.getId()
    );
    return spend;
  }

  @NotNull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    try {
      return Optional.ofNullable(
          jdbcTemplate.queryForObject(
              """
                     SELECT * FROM "spend" WHERE id = ?
                  """,
              SpendEntityRowMapper.instance,
              id
          )
      );
    } catch (EmptyResultDataAccessException e) {
      return Optional.empty();
    }
  }

  @NotNull
  @Override
  public List<SpendEntity> findAllByUsername(String username) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return jdbcTemplate.query(
        """
               SELECT * FROM "spend" WHERE username = ?
            """,
        SpendEntityRowMapper.instance,
        username
    );
  }

  @Override
  public void delete(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    jdbcTemplate.update(
        """
               DELETE * FROM "spend" WHERE id = ?
            """,
        spend.getId());
  }

  @NotNull
  @Override
  public List<SpendEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return jdbcTemplate.query(
        """
               SELECT * FROM "spend"
            """,
        SpendEntityRowMapper.instance
    );
  }

  @NotNull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(url));
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            """
                   SELECT * FROM "spend" WHERE username = ? and description = ?
                """,
            SpendEntityRowMapper.instance,
            username,
            description
        )
    );
  }
}
