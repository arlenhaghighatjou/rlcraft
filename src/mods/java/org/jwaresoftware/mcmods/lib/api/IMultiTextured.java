package org.jwaresoftware.mcmods.lib.api;

public interface IMultiTextured {
   default int[] getInventoryRenderingMetas() {
      return new int[]{0};
   }

   String[] getInventoryRenderingNames();
}
