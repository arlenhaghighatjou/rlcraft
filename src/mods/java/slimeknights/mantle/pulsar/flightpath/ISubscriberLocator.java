package slimeknights.mantle.pulsar.flightpath;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public interface ISubscriberLocator {
   @Nonnull
   Map<Class, Set<Method>> findSubscribers(Object var1);
}
