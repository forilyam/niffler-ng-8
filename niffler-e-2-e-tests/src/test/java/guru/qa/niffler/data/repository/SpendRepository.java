package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.jdbc.SpendRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.spring.SpendRepositorySpringJdbc;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendRepository {

  @Nonnull
  static SpendRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jdbc")) {
      case "jpa" -> new SpendRepositoryHibernate();
      case "jdbc" -> new SpendRepositoryJdbc();
      case "sjdbc" -> new SpendRepositorySpringJdbc();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  @Nonnull
  SpendEntity create(SpendEntity spend);

  @Nonnull
  SpendEntity update(SpendEntity spend);

  @Nonnull
  CategoryEntity createCategory(CategoryEntity category);

  @Nonnull
  Optional<CategoryEntity> findCategoryById(UUID id);

  @Nonnull
  Optional<CategoryEntity> findCategoryByUsernameAndCategoryName(String username, String name);

  @Nonnull
  Optional<SpendEntity> findById(UUID id);

  @Nonnull
  Optional<SpendEntity> findByUsernameAndSpendDescription(String username, String description);

  void remove(SpendEntity spend);

  void removeCategory(CategoryEntity category);
}