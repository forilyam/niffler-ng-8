package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spending;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.CategoryJson;
import guru.qa.niffler.model.rest.SpendJson;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.SpendClient;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class SpendingExtension implements BeforeEachCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);

  private final SpendClient spendClient = SpendClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.spendings())) {
            final UserJson createdUser = UserExtension.getUserJson();
            final String username = createdUser != null
                ? createdUser.username()
                : userAnno.username();

            final List<CategoryJson> existingCategories = createdUser != null
                ? createdUser.testData().categories()
                : CategoryExtension.createdCategories();

            final List<SpendJson> createdSpendings = new ArrayList<>();

            for (Spending spendAnno : userAnno.spendings()) {
              final Optional<CategoryJson> matchedCategory = existingCategories.stream()
                  .filter(cat -> cat.name().equals(spendAnno.category()))
                  .findFirst();

              SpendJson spend = new SpendJson(
                  null,
                  new Date(),
                  matchedCategory.orElseGet(() -> new CategoryJson(
                      null,
                      spendAnno.category(),
                      username,
                      false
                  )),
                  spendAnno.currency(),
                  spendAnno.amount(),
                  spendAnno.description(),
                  username
              );

              createdSpendings.add(
                  spendClient.create(spend)
              );
            }
            if (createdUser != null) {
              createdUser.testData().spendings().addAll(
                  createdSpendings
              );
            } else {
              context.getStore(NAMESPACE).put(
                  context.getUniqueId(),
                  createdSpendings
              );
            }
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (SpendJson[]) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), List.class)
        .stream()
        .toArray(SpendJson[]::new);
  }
}