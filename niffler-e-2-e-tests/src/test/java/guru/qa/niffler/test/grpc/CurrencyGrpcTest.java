package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

import static guru.qa.niffler.grpc.CurrencyValues.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CurrencyGrpcTest extends BaseGrpcTest {

  @Test
  void allCurrenciesShouldReturned() {
    final CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
    final List<Currency> allCurrenciesList = response.getAllCurrenciesList();
    assertEquals(4, allCurrenciesList.size());
  }

  @Nonnull
  private static Stream<Arguments> currencies() {
    return Stream.of(
        Arguments.of(RUB, USD, 100.0, 1.5),
        Arguments.of(USD, EUR, 100.0, 92.59),
        Arguments.of(EUR, KZT, 100.0, 51428.57),
        Arguments.of(KZT, RUB, 100.0, 14.0)
    );
  }

  @MethodSource("currencies")
  @ParameterizedTest
  void shouldCalculateRatesForDifferentCurrencies(CurrencyValues spendCurrency,
                                                  CurrencyValues desiredCurrency,
                                                  double amount,
                                                  double calculatedAmount) {
    final CalculateRequest request = CalculateRequest.newBuilder()
        .setSpendCurrency(spendCurrency)
        .setDesiredCurrency(desiredCurrency)
        .setAmount(amount)
        .build();

    final CalculateResponse response = blockingStub.calculateRate(request);
    assertEquals(calculatedAmount, response.getCalculatedAmount());
  }

  @Test
  void shouldNotCalculateRatesForUnspecifiedCurrency() {
    final CalculateRequest request = CalculateRequest.newBuilder()
        .setSpendCurrency(UNSPECIFIED)
        .setDesiredCurrency(RUB)
        .setAmount(100.0)
        .build();

    Assertions.assertThrows(
        StatusRuntimeException.class,
        () -> blockingStub.calculateRate(request),
        "Currency is unspecified");
  }
}