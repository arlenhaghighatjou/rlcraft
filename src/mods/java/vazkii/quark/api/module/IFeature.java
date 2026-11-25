package vazkii.quark.api.module;

import javax.annotation.Nonnull;

public interface IFeature extends Comparable<IFeature> {
   IModule getModule();

   boolean isEnabled();

   boolean isClient();

   String getName();

   default int compareTo(@Nonnull IFeature o) {
      return this.getName().compareTo(o.getName());
   }
}
