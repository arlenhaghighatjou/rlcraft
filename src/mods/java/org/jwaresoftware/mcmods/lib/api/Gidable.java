package org.jwaresoftware.mcmods.lib.api;

import javax.annotation.Nonnull;

public interface Gidable extends Oidable {
   @Nonnull
   default String gid(@Nonnull Object object) {
      return "";
   }
}
