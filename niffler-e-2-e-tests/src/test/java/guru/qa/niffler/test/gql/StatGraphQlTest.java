package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CategoriesQuery;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.CurrencyValues;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class StatGraphQlTest extends BaseGraphQlTest {

  @User
  @Test
  @ApiLogin
  void statTest(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> currenciesCall = apolloClient.query(StatQuery.builder()
            .filterCurrency(null)
            .statCurrency(null)
            .filterPeriod(null)
            .build())
        .addHttpHeader("authorization", "Bearer " + bearerToken);

    final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(currenciesCall).blockingGet();
    final StatQuery.Data data = response.dataOrThrow();
    StatQuery.Stat result = data.stat;
    Assertions.assertEquals(
        0.0,
        result.total
    );
  }

  @User(
      categories = {
          @Category(name = "Обучение"),
          @Category(name = "Техника", archived = true)
      },
      spendings = {
          @Spending(
              category = "Обучение",
              description = "Обучение Advanced 2.0",
              amount = 1000,
              currency = CurrencyValues.USD
          ),
          @Spending(
              category = "Техника",
              description = "Телевизор",
              amount = 800,
              currency = CurrencyValues.EUR
          )
      }
  )
  @ApiLogin
  @Test
  void currenciesShouldBeConverted(@Token String bearerToken) {
    final ApolloCall<StatQuery.Data> statCall = apolloClient.query(StatQuery.builder()
            .filterCurrency(null)
            .statCurrency(null)
            .filterPeriod(null)
            .build())
        .addHttpHeader("authorization", "Bearer " + bearerToken);

    final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(statCall).blockingGet();
    final StatQuery.Data data = response.dataOrThrow();
    final StatQuery.Stat result = data.stat;

    Assertions.assertEquals(124266.67, result.total);
    Assertions.assertEquals(CurrencyValues.RUB.name(), result.currency.rawValue);
  }

  @User(
      categories = {
          @Category(name = "Обучение"),
          @Category(name = "Техника", archived = true)
      },
      spendings = {
          @Spending(
              category = "Обучение",
              description = "Обучение Advanced 2.0",
              amount = 1000,
              currency = CurrencyValues.USD
          ),
          @Spending(
              category = "Техника",
              description = "Телевизор",
              amount = 800,
              currency = CurrencyValues.EUR
          )
      }
  )
  @ApiLogin
  @Test
  void archivedCategoriesShouldBeReturnedFromGateway(@Token String bearerToken) {
    ApolloCall<CategoriesQuery.Data> categoriesCall = apolloClient.query(CategoriesQuery.builder().build())
        .addHttpHeader("authorization", "Bearer " + bearerToken);

    final ApolloResponse<CategoriesQuery.Data> categoriesResponse = Rx2Apollo.single(categoriesCall).blockingGet();
    final CategoriesQuery.Data categoriesData = categoriesResponse.dataOrThrow();
    assertTrue(
        categoriesData.user.categories.stream()
            .filter(c -> c.name.equals("Техника"))
            .findFirst().get().archived);

    ApolloCall<StatQuery.Data> statCall = apolloClient.query(StatQuery.builder()
        .filterCurrency(null)
        .statCurrency(null)
        .filterPeriod(null)
        .build()
    ).addHttpHeader("authorization", "Bearer " + bearerToken);

    final ApolloResponse<StatQuery.Data> statResponse = Rx2Apollo.single(statCall).blockingGet();
    final StatQuery.Data data = statResponse.dataOrThrow();
    assertTrue(
        data.stat.statByCategories.stream()
            .anyMatch(c -> c.categoryName.equals("Archived")));
  }
}