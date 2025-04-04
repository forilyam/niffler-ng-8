package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.Optional;

import static guru.qa.niffler.utils.RandomDataUtils.randomCategoryName;

public class CategoryExtension implements BeforeEachCallback, AfterTestExecutionCallback, ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if (ArrayUtils.isNotEmpty(userAnno.categories())) {
            CategoryJson category = new CategoryJson(
                null,
                randomCategoryName(),
                userAnno.username(),
                userAnno.categories()[0].archived()
            );

            CategoryJson created = spendApiClient.createCategory(category);
            if (userAnno.categories()[0].archived()) {
              CategoryJson archivedCategory = new CategoryJson(
                  created.id(),
                  created.name(),
                  created.username(),
                  true
              );
              created = spendApiClient.updateCategory(archivedCategory);
            }

            context.getStore(NAMESPACE).put(context.getUniqueId(), created);
          }
        });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    Optional<CategoryJson> categoryJson = Optional.ofNullable(context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class));
    categoryJson.ifPresent(category -> {
      if (category.archived()) {
        CategoryJson archivedCategory = new CategoryJson(
            category.id(),
            category.name(),
            category.username(),
            true
        );
        spendApiClient.updateCategory(archivedCategory);
      }
    });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
  }

  @Override
  public CategoryJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(CategoryExtension.NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
  }
}
