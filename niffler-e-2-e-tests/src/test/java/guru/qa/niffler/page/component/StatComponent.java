package guru.qa.niffler.page.component;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StatComponent {

  public final SelenideElement self = $("#stat");
  private final ElementsCollection bubbles = self.$("#legend-container").$$("li");
  private final SelenideElement statisticCanvas = $("canvas[role='img']");

  public StatComponent checkStatisticBubblesContains(String... texts) {
    bubbles.should(CollectionCondition.texts(texts));
    return this;
  }

  public StatComponent checkStatisticImage(BufferedImage expectedImage) throws IOException {
    Selenide.sleep(3000);
    BufferedImage actualImage = ImageIO.read(Objects.requireNonNull(statisticCanvas.screenshot()));
    assertFalse(
        new ScreenDiffResult(
            actualImage,
            expectedImage
        )
    );
    return this;
  }
}