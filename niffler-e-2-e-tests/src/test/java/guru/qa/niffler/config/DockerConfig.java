package guru.qa.niffler.config;

import javax.annotation.Nonnull;
import java.util.Objects;

enum DockerConfig implements Config {
  INSTANCE;

  @Override
  public String frontUrl() {
    return "http://frontend.niffler.dc/";
  }

  @Override
  public String authUrl() {
    return "http://auth.niffler.dc:9000/";
  }

  @Override
  public String authJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-auth";
  }

  @Override
  public String gatewayUrl() {
    return "http://gateway.niffler.dc:8090/";
  }

  @Override
  public String userdataUrl() {
    return "http://userdata.niffler.dc:8089/";
  }

  @Override
  public String userdataJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-userdata";
  }

  @Override
  public String spendUrl() {
    return "http://spend.niffler.dc:8093/";
  }

  @Override
  public String spendJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-spend";
  }

  @Override
  public String currencyJdbcUrl() {
    return "jdbc:postgresql://niffler-all-db:5432/niffler-currency";
  }

  @Nonnull
  @Override
  public String currencyGrpcAddress() {
    return "currency.niffler.dc";
  }

  @Nonnull
  @Override
  public String allureDockerServiceUrl() {
    String allureDockerApiUrl = System.getenv("ALLURE_DOCKER_API");
    return Objects.requireNonNullElse(allureDockerApiUrl, "http://allure:5050/");
  }
}
