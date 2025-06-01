package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import static com.codeborne.selenide.Selenide.$;

@ParametersAreNonnullByDefault
public abstract class BasePage<T extends BasePage<?>> {

  protected static final Config CFG = Config.getInstance();

  private final SelenideElement alert = $(".MuiSnackbar-root");

  public abstract T checkThatPageLoaded();

  @Step("Check that alert message appears: {expectedText}")
  @SuppressWarnings("unchecked")
  @Nonnull
  public T checkAlertMessage(String expectedText) {
    alert.should(Condition.visible).should(Condition.text(expectedText));
    return (T) this;
  }
}