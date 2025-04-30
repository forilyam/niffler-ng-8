package guru.qa.niffler.service;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.util.Optional;
import java.util.UUID;

public interface SpendClient {

  SpendJson create(SpendJson spend);

  SpendJson update(SpendJson spend);

  CategoryJson createCategory(CategoryJson category);

  Optional<CategoryJson> findCategoryById(UUID id);

  Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String name);

  Optional<SpendJson> findById(UUID id);

  Optional<SpendJson> findByUsernameAndSpendDescription(String username, String description);

  void remove(SpendJson spend);

  void removeCategory(CategoryJson category);
}
