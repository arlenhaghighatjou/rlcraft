package c4.champions.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionInjured extends Potion {
   private static final ResourceLocation ICON = new ResourceLocation("champions", "textures/gui/injured.png");

   public PotionInjured() {
      super(true, 9240631);
      this.setPotionName("champions.injured");
      this.setRegistryName("champions", "injured");
   }

   @SideOnly(Side.CLIENT)
   public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
      if (mc.currentScreen != null) {
         mc.getTextureManager().bindTexture(ICON);
         Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
      }

   }

   @SideOnly(Side.CLIENT)
   public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
      mc.getTextureManager().bindTexture(ICON);
      Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0.0F, 0.0F, 18, 18, 18.0F, 18.0F);
   }
}
