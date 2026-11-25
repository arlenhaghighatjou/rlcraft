package vazkii.quark.api.module;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

public interface IModule extends Comparable<IModule> {
   String getName();

   boolean isEnabled();

   Map<String, ? extends IFeature> getFeatures();

   List<? extends IFeature> getEnabledFeatures();

   default int compareTo(@Nonnull IModule o) {
      return this.getName().compareTo(o.getName());
   }
}
