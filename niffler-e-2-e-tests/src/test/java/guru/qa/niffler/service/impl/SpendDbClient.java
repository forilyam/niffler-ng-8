package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.hibernate.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

import java.util.Optional;
import java.util.UUID;

public class SpendDbClient implements SpendClient {

  private static final Config CFG = Config.getInstance();

  private final SpendRepository spendRepository = new SpendRepositoryHibernate();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Override
  public SpendJson create(SpendJson spend) {
    return xaTransactionTemplate.execute(() -> SpendJson.fromEntity(
            spendRepository.create(SpendEntity.fromJson(spend))
        )
    );
  }

  @Override
  public SpendJson update(SpendJson spend) {
    return xaTransactionTemplate.execute(() -> SpendJson.fromEntity(
            spendRepository.update(SpendEntity.fromJson(spend))
        )
    );
  }

  public CategoryJson createCategory(CategoryJson category) {
    return xaTransactionTemplate.execute(() -> CategoryJson.fromEntity(
            spendRepository.createCategory(CategoryEntity.fromJson(category))
        )
    );
  }

  @Override
  public Optional<CategoryJson> findCategoryById(UUID id) {
    return xaTransactionTemplate.execute(() -> {
      Optional<CategoryEntity> category = spendRepository.findCategoryById(id);
      return category.map(CategoryJson::fromEntity);
    });
  }

  @Override
  public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String name) {
    return xaTransactionTemplate.execute(() -> {
      Optional<CategoryEntity> category = spendRepository.findCategoryByUsernameAndCategoryName(username, name);
      return category.map(CategoryJson::fromEntity);
    });
  }

  @Override
  public Optional<SpendJson> findById(UUID id) {
    return xaTransactionTemplate.execute(() -> {
      Optional<SpendEntity> spend = spendRepository.findById(id);
      return spend.map(SpendJson::fromEntity);
    });
  }

  @Override
  public Optional<SpendJson> findByUsernameAndSpendDescription(String username, String description) {
    return xaTransactionTemplate.execute(() -> {
      Optional<SpendEntity> spend = spendRepository.findByUsernameAndSpendDescription(username, description);
      return spend.map(SpendJson::fromEntity);
    });
  }

  @Override
  public void remove(SpendJson spend) {
    xaTransactionTemplate.execute(() -> {
      SpendEntity spendEntity = SpendEntity.fromJson(spend);
      spendRepository.remove(spendEntity);
      return null;
    });
  }

  @Override
  public void removeCategory(CategoryJson category) {
    xaTransactionTemplate.execute(() -> {
      CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
      spendRepository.removeCategory(categoryEntity);
      return null;
    });
  }
}