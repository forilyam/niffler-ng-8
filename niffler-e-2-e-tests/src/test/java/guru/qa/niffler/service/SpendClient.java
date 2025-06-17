package guru.qa.niffler.service;

import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.service.impl.SpendDbClient;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public interface SpendClient {

  static SpendClient getInstance() {
    return "api".equals(System.getProperty("client.impl"))
        ? new SpendApiClient()
        : new SpendDbClient();
  }

  @Nonnull
  SpendJson create(SpendJson spend);

  @Nonnull
  SpendJson update(SpendJson spend);

  @Nonnull
  CategoryJson createCategory(CategoryJson category);

  @Nonnull
  Optional<CategoryJson> findCategoryById(UUID id);

  @Nonnull
  Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String name);

  @Nonnull
  Optional<SpendJson> findById(UUID id);

  @Nonnull
  Optional<SpendJson> findByUsernameAndSpendDescription(String username, String description);

  void remove(SpendJson spend);

  void removeCategory(CategoryJson category);
}
