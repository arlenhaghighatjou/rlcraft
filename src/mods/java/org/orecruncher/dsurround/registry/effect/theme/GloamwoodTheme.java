package org.orecruncher.dsurround.registry.effect.theme;

import javax.annotation.Nonnull;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GloamwoodTheme extends ThemeInfo {
   public GloamwoodTheme() {
      this.doMorningFog = false;
      this.minFogDistance = 20.0F;
      this.maxFogDistance = 60.0F;
   }

   @Nonnull
   public String name() {
      return "Gloamwood";
   }
}
