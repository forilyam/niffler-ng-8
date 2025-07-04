package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.rest.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class UserExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
  private static final String defaultPassword = "12345";

  private final UsersClient usersClient = UsersClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(userAnno -> {
          if ("".equals(userAnno.username())) {
            final String username = RandomDataUtils.randomUsername();

            UserJson user = usersClient.createUser(
                username,
                defaultPassword
            );
            usersClient.createIncomeInvitations(user, userAnno.incomeInvitations());
            usersClient.createOutcomeInvitations(user, userAnno.outcomeInvitations());
            usersClient.createFriends(user, userAnno.friends());
            setUser(user);
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return getUserJson();
  }

  public static UserJson getUserJson() {
    final ExtensionContext context = TestsMethodContextExtension.context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
  }

  public static void setUser(UserJson user) {
    final ExtensionContext context = TestsMethodContextExtension.context();
    context.getStore(NAMESPACE)
        .put(context.getUniqueId(), user.withPassword(defaultPassword));
  }
}