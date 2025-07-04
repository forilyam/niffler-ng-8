package guru.qa.niffler.data.repository.impl.jdbc;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class SpendRepositoryJdbc implements SpendRepository {

  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  @NotNull
  @Override
  public SpendEntity create(SpendEntity spend) {
    final UUID categoryId = spend.getCategory().getId();
    if (categoryId == null || categoryDao.findById(categoryId).isEmpty()) {
      spend.setCategory(
          categoryDao.create(spend.getCategory())
      );
    }
    return spendDao.create(spend);
  }

  @NotNull
  @Override
  public SpendEntity update(SpendEntity spend) {
    spendDao.update(spend);
    categoryDao.update(spend.getCategory());
    return spend;
  }

  @NotNull
  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    return categoryDao.create(category);
  }

  @NotNull
  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return categoryDao.findById(id);
  }

  @NotNull
  @Override
  public Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name) {
    return categoryDao.findCategoryByUsernameAndCategoryName(username, name);
  }

  @NotNull
  @Override
  public Optional<SpendEntity> findById(UUID id) {
    return spendDao.findById(id);
  }

  @NotNull
  @Override
  public Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description) {
    return spendDao.findByUsernameAndSpendDescription(username, description);
  }

  @Override
  public void remove(SpendEntity spend) {
    spendDao.delete(spend);
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    categoryDao.delete(category);
  }
}
