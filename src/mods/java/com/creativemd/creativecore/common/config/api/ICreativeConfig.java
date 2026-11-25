package com.creativemd.creativecore.common.config.api;

import net.minecraftforge.fml.relauncher.Side;

public interface ICreativeConfig {
   void configured();

   default void configured(Side side) {
      this.configured();
   }
}
