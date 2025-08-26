package guru.qa.niffler.config;

import javax.annotation.Nonnull;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.INSTANCE
        : LocalConfig.INSTANCE;
  }

  String frontUrl();

  String authUrl();

  String authJdbcUrl();

  String gatewayUrl();

  String userdataUrl();

  String userdataJdbcUrl();

  String spendUrl();

  String spendJdbcUrl();

  String currencyJdbcUrl();

  @Nonnull
  String allureDockerServiceUrl();

  @Nonnull
  String screenshotBaseDir();

  @Nonnull
  String currencyGrpcAddress();

  default int currencyGrpcPort() {
    return 8092;
  }

  @Nonnull
  default String ghUrl() {
    return "https://api.github.com/";
  }
}
