package rustic.common.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionBase extends Potion {
   public static ResourceLocation POTION_ICONS = new ResourceLocation("rustic:textures/gui/potion_icons.png");

   public PotionBase(boolean isBadEffect, int color, String name) {
      super(isBadEffect, color);
      this.setPotionName("effect." + name);
      if (!isBadEffect) {
         this.setBeneficial();
      }

   }

   public PotionBase setIconIndex(int x, int y) {
      super.setIconIndex(x, y);
      return this;
   }

   @SideOnly(Side.CLIENT)
   public int getStatusIconIndex() {
      Minecraft.getMinecraft().getTextureManager().bindTexture(POTION_ICONS);
      return super.getStatusIconIndex();
   }
}
