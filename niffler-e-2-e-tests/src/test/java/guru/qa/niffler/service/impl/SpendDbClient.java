package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

@ParametersAreNonnullByDefault
public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final SpendRepository spendRepository = SpendRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Step("Create spend using SQL INSERT")
  @Nonnull
  @Override
  public SpendJson create(SpendJson spend) {
    return requireNonNull(xaTransactionTemplate.execute(() -> SpendJson.fromEntity(
            spendRepository.create(SpendEntity.fromJson(spend))
        )
    ));
  }

  @Step("Update spend using SQL INSERT")
  @Nonnull
  @Override
  public SpendJson update(SpendJson spend) {
    return requireNonNull(xaTransactionTemplate.execute(() -> SpendJson.fromEntity(
            spendRepository.update(SpendEntity.fromJson(spend))
        )
    ));
  }

  @Step("Create category using SQL INSERT")
  @Nonnull
  public CategoryJson createCategory(CategoryJson category) {
    return requireNonNull(xaTransactionTemplate.execute(() -> CategoryJson.fromEntity(
            spendRepository.createCategory(CategoryEntity.fromJson(category))
        )
    ));
  }

  @Step("Find category using SQL SELECT")
  @Nonnull
  @Override
  public Optional<CategoryJson> findCategoryById(UUID id) {
    return requireNonNull(xaTransactionTemplate.execute(() -> {
      Optional<CategoryEntity> category = spendRepository.findCategoryById(id);
      return category.map(CategoryJson::fromEntity);
    }));
  }

  @Step("Find category using SQL SELECT")
  @Nonnull
  @Override
  public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String name) {
    return requireNonNull(xaTransactionTemplate.execute(() -> {
      Optional<CategoryEntity> category = spendRepository.findCategoryByUsernameAndCategoryName(username, name);
      return category.map(CategoryJson::fromEntity);
    }));
  }

  @Step("Find spend using SQL SELECT")
  @Nonnull
  @Override
  public Optional<SpendJson> findById(UUID id) {
    return requireNonNull(xaTransactionTemplate.execute(() -> {
      Optional<SpendEntity> spend = spendRepository.findById(id);
      return spend.map(SpendJson::fromEntity);
    }));
  }

  @Step("Find spend using SQL SELECT")
  @Nonnull
  @Override
  public Optional<SpendJson> findByUsernameAndSpendDescription(String username, String description) {
    return requireNonNull(xaTransactionTemplate.execute(() -> {
      Optional<SpendEntity> spend = spendRepository.findByUsernameAndSpendDescription(username, description);
      return spend.map(SpendJson::fromEntity);
    }));
  }

  @Step("Remove spend using SQL DELETE")
  @Override
  public void remove(SpendJson spend) {
    xaTransactionTemplate.execute(() -> {
      SpendEntity spendEntity = SpendEntity.fromJson(spend);
      spendRepository.remove(spendEntity);
      return null;
    });
  }

  @Step("Remove category using SQL DELETE")
  @Override
  public void removeCategory(CategoryJson category) {
    xaTransactionTemplate.execute(() -> {
      CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
      spendRepository.removeCategory(categoryEntity);
      return null;
    });
  }
}