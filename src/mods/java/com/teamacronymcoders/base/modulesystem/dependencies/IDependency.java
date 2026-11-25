package com.teamacronymcoders.base.modulesystem.dependencies;

import com.teamacronymcoders.base.modulesystem.ModuleHandler;
import javax.annotation.Nullable;

public interface IDependency {
   boolean isMet(ModuleHandler var1);

   default boolean isSilent() {
      return false;
   }

   @Nullable
   default String notMetMessage() {
      return null;
   }
}
