package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient implements SpendClient {

  private final SpendApi spendApi;

  public SpendApiClient() {
    super(CFG.spendUrl());
    this.spendApi = create(SpendApi.class);
  }

  public void deleteSpends(String username, List<String> ids) {
    final Response<String> response;
    try {
      response = spendApi.deleteSpends(username, ids)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  @Step("Create spend using REST API")
  @Nonnull
  @Override
  public SpendJson create(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.addSpend(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(201, response.code());
    return requireNonNull(response.body());
  }

  @Step("Update spend using REST API")
  @Nonnull
  @Override
  public SpendJson update(SpendJson spend) {
    final Response<SpendJson> response;
    try {
      response = spendApi.editSpend(spend)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return requireNonNull(response.body());
  }

  @Step("Create category using REST API")
  @Nonnull
  public CategoryJson createCategory(CategoryJson category) {
    final Response<CategoryJson> response;
    try {
      response = spendApi.addCategory(category)
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
    return requireNonNull(response.body());
  }

  @Nonnull
  @Override
  public Optional<CategoryJson> findCategoryById(UUID id) {
    throw new UnsupportedOperationException("Method 'findCategoryById' is not implemented");
  }

  @Nonnull
  @Override
  public Optional<CategoryJson> findCategoryByUsernameAndCategoryName(String username, String name) {
    throw new UnsupportedOperationException("Method 'findCategoryByUsernameAndCategoryName' is not implemented");
  }

  @Nonnull
  @Override
  public Optional<SpendJson> findById(UUID id) {
    throw new UnsupportedOperationException("Method 'findById' is not implemented");

  }

  @Nonnull
  @Override
  public Optional<SpendJson> findByUsernameAndSpendDescription(String username, String description) {
    throw new UnsupportedOperationException("Method 'findByUsernameAndSpendDescription' is not implemented");
  }

  @Step("Remove spend using REST API")
  @Override
  public void remove(SpendJson spend) {
    final Response<String> response;
    try {
      response = spendApi.deleteSpends(spend.username(), List.of(String.valueOf(spend.id())))
          .execute();
    } catch (IOException e) {
      throw new AssertionError(e);
    }
    assertEquals(200, response.code());
  }

  @Step("Remove category using REST API")
  @Override
  public void removeCategory(CategoryJson category) {
    throw new UnsupportedOperationException("Method 'removeCategory' is not implemented");
  }
}
