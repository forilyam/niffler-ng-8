package guru.qa.niffler.test.web;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.OAuthUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import retrofit2.Response;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {

  private final AuthApi authApi = new RestClient.EmtyRestClient(CFG.authUrl(), true).create(AuthApi.class);

  private static final Config CFG = Config.getInstance();

  @SneakyThrows
  @Test
  @User
  void oauthTest(UserJson user) {
    final String codeVerifier = OAuthUtils.generateCodeVerifier();
    final String codeChallenge = OAuthUtils.generateCodeChallange(codeVerifier);
    final String redirectUri = CFG.frontUrl() + "authorized";
    final String clientId = "client";

    authApi.requestRegisterForm().execute();

    authApi.authorize(
        "code",
        clientId,
        "openid",
        redirectUri,
        codeChallenge,
        "S256"
    ).execute();

    Response<Void> loginResponse = authApi.login(
        user.username(),
        user.testData().password(),
        ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
    ).execute();

    String code = StringUtils.substringAfter(
        String.valueOf(loginResponse.raw().request().url()), "code=");

    Response<JsonNode> tokenResponse = authApi.token(
        code,
        redirectUri,
        clientId,
        codeVerifier,
        "authorization_code"
    ).execute();

    String token = tokenResponse.body().get("id_token").asText();
    assertNotNull(token);
  }
}
