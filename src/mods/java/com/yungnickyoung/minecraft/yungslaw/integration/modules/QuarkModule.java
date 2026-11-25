package com.yungnickyoung.minecraft.yungslaw.integration.modules;

import com.yungnickyoung.minecraft.yungslaw.YungsLaw;
import com.yungnickyoung.minecraft.yungslaw.config.Configuration;
import java.util.Objects;
import net.minecraft.block.state.IBlockState;
import vazkii.quark.world.feature.Biotite;

public class QuarkModule extends CompatModule {
   public QuarkModule() {
      super("quark");
   }

   public void enable() {
      super.enable();

      try {
         IBlockState biotite = (IBlockState)Objects.requireNonNull(Biotite.biotite_ore.getDefaultState());
         this.addIfAbsentAndAble(this.ores, biotite);
      } catch (Exception var2) {
         YungsLaw.LOGGER.error("Unable to register Quark Biotite for YUNG's Law! Is the Quark Biotite module enabled?");
         YungsLaw.LOGGER.error(var2.toString());
      }

   }

   public boolean shouldBeEnabled() {
      return super.shouldBeEnabled() && Configuration.modCompat.quark;
   }
}
