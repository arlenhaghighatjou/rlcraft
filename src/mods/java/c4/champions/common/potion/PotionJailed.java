package c4.champions.common.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionJailed extends Potion {
   private static final ResourceLocation ICON = new ResourceLocation("champions", "textures/gui/jailed.png");

   public PotionJailed() {
      super(true, 0);
      this.setPotionName("champions.jailed");
      this.setRegistryName("champions", "jailed");
      this.registerPotionAttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "2e1d5db6-1bb0-49a7-907d-2e5531d04736", 1.0D, 0);
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
