package c4.champions.common.potion;

import c4.champions.common.config.ConfigHandler;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionPlague extends Potion {
   private static final ResourceLocation ICON = new ResourceLocation("champions", "textures/gui/plague.png");
   private static Potion INFECTION = MobEffects.WITHER;

   public PotionPlague() {
      super(true, 2046740);
      this.setPotionName("champions.plague");
      this.setRegistryName("champions", "plague");
   }

   public static void setInfectionPotion(Potion potion) {
      INFECTION = potion;
   }

   public static Potion getInfectionPotion() {
      return INFECTION;
   }

   public boolean isReady(int duration, int amplifier) {
      return true;
   }

   public void performEffect(@Nonnull EntityLivingBase entityLivingBaseIn, int amplifier) {
      if (!entityLivingBaseIn.world.isRemote) {
         if (!entityLivingBaseIn.isPotionActive(INFECTION)) {
            entityLivingBaseIn.addPotionEffect(new PotionEffect(INFECTION, ConfigHandler.affix.plagued.infectDuration, ConfigHandler.affix.plagued.infectPower - 1));
         }

         if (entityLivingBaseIn instanceof EntityPlayer) {
            ((EntityPlayer)entityLivingBaseIn).addExhaustion(0.0025F);
         }
      }

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
