package guru.qa.niffler.data.extractor;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CurrencyValues;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityRowExtractor implements ResultSetExtractor<SpendEntity> {

  public static final SpendEntityRowExtractor instance = new SpendEntityRowExtractor();

  private SpendEntityRowExtractor() {
  }

  /**
   * select
   * c.id as category_id,
   * c.name as category_name,
   * c.archived as category_archived,
   * s.id,
   * s.username,
   * s.spend_date,
   * s.currency,
   * s.amount,
   * s.description
   * from spend s join category c on s.category_id = c.id
   * where s.id = ?
   */
  @Nullable
  @Override
  public SpendEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
    SpendEntity result = new SpendEntity();
    result.setId(rs.getObject("id", UUID.class));
    result.setUsername(rs.getString("username"));
    result.setSpendDate(rs.getDate("spend_date"));
    result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
    result.setAmount(rs.getDouble("amount"));
    result.setDescription(rs.getString("description"));

    CategoryEntity category = new CategoryEntity(rs.getObject("category_id", UUID.class));
    category.setUsername(rs.getString("username"));
    category.setName(rs.getString("category_name"));
    category.setArchived(rs.getBoolean("category_archived"));

    result.setCategory(category);
    return result;
  }
}