package guru.qa.niffler.page.component;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.Month;
import java.util.Date;
import java.util.GregorianCalendar;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static java.util.Calendar.*;

public class Calendar extends BaseComponent<Calendar> {

  private final SelenideElement calendarButton = $("button[aria-label*='Choose date']");
  private final SelenideElement currentMonthAndYear = self.$(".MuiPickersCalendarHeader-label");
  private final ElementsCollection selectYear = self.$$(".MuiPickersYear-yearButton");
  private final SelenideElement prevMonthButton = self.$("button[title='Previous month']");
  private final SelenideElement nextMonthButton = self.$("button[title='Next month']");
  private final ElementsCollection days = $$(".MuiPickersSlideTransition-root button");

  public Calendar() {
    super($(".MuiPickersLayout-root"));
  }

  @Step("Select date in calendar: '{date}'")
  public void selectDateInCalendar(Date date) {
    java.util.Calendar calendar = new GregorianCalendar();
    calendar.setTime(date);
    calendarButton.click();
    final int desiredMonthIndex = calendar.get(MONTH);
    selectYear(calendar.get(YEAR));
    selectMonth(desiredMonthIndex);
    selectDay(calendar.get(DAY_OF_MONTH));
  }

  private void selectYear(int expectedYear) {
    currentMonthAndYear.click();
    selectYear.findBy(text(String.valueOf(expectedYear))).click();
  }

  private void selectMonth(int expectedMonth) {
    int actualMonth = getActualMonth();

    while (actualMonth > expectedMonth) {
      prevMonthButton.click();
      currentMonthAndYear.shouldBe(visible);
      actualMonth = getActualMonth();
    }
    while (actualMonth < expectedMonth) {
      nextMonthButton.click();
      currentMonthAndYear.shouldBe(visible);
      actualMonth = getActualMonth();
    }
  }

  private int getActualMonth() {
    return Month.valueOf(currentMonthAndYear.getText()
            .split(" ")[0]
            .toUpperCase())
        .ordinal();
  }

  private void selectDay(int expectedDay) {
    days.findBy(text(String.valueOf(expectedDay))).click();
  }
}