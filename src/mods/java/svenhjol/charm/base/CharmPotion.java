package svenhjol.charm.base;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import svenhjol.meson.MesonPotion;

public class CharmPotion extends MesonPotion {
   public static ResourceLocation TEXTURE = new ResourceLocation("charm", "textures/misc/potions.png");

   public CharmPotion(String name, boolean badEffect, int color, int iconIndex) {
      super(name, badEffect, color);
      this.setIconIndex(iconIndex % 8, iconIndex / 8);
   }

   public String getModId() {
      return "charm";
   }

   @SideOnly(Side.CLIENT)
   public int getStatusIconIndex() {
      Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
      return super.getStatusIconIndex();
   }
}
