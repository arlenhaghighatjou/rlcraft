package vazkii.quark.client.feature;

import betterwithmods.api.FeatureEnabledEvent;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.Optional.Method;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;

public class FoodTooltip extends Feature {
   public static int divisor = 2;

   public FoodTooltip() {
   }

   @SubscribeEvent
   @Method(
      modid = "betterwithmods"
   )
   public void bwmFeatureEnabled(FeatureEnabledEvent event) {
      if (event.getFeature().equals("hchunger") && event.isEnabled()) {
         divisor = 12;
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void makeTooltip(ItemTooltipEvent event) {
      if (!event.getItemStack().isEmpty() && event.getItemStack().getItem() instanceof ItemFood) {
         int pips = ((ItemFood)event.getItemStack().getItem()).getHealAmount(event.getItemStack());
         int len = (int)Math.ceil((double)pips / (double)divisor);
         StringBuilder s = new StringBuilder(" ");

         for(int i = 0; i < len; ++i) {
            s.append("  ");
         }

         List<String> tooltip = event.getToolTip();
         if (tooltip.isEmpty()) {
            tooltip.add(s.toString());
         } else {
            tooltip.add(1, s.toString());
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderTooltip(PostText event) {
      if (!event.getStack().isEmpty() && event.getStack().getItem() instanceof ItemFood) {
         GlStateManager.pushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         Minecraft mc = Minecraft.getMinecraft();
         mc.getTextureManager().bindTexture(GuiIngameForge.ICONS);
         ItemFood food = (ItemFood)event.getStack().getItem();
         int pips = food.getHealAmount(event.getStack());
         PotionEffect eff = (PotionEffect)ObfuscationReflectionHelper.getPrivateValue(ItemFood.class, food, "potionId");
         boolean poison = eff != null && eff.getPotion().isBadEffect();
         int count = (int)Math.ceil((double)pips / (double)divisor);
         int y = shiftTextByLines(event.getLines(), event.getY() + 10);

         for(int i = 0; i < count; ++i) {
            int x = event.getX() + i * 9 - 2;
            int u = 16;
            if (poison) {
               u += 117;
            }

            int v = 27;
            Gui.drawModalRectWithCustomSizedTexture(x, y, (float)u, (float)v, 9, 9, 256.0F, 256.0F);
            u = 52;
            if (pips % 2 != 0 && i == 0) {
               u += 9;
            }

            if (poison) {
               u += 36;
            }

            Gui.drawModalRectWithCustomSizedTexture(x, y, (float)u, (float)v, 9, 9, 256.0F, 256.0F);
         }

         GlStateManager.popMatrix();
      }

   }

   public static int shiftTextByLines(List<String> lines, int y) {
      for(int i = 1; i < lines.size(); ++i) {
         String s = (String)lines.get(i);
         s = TextFormatting.getTextWithoutFormattingCodes(s);
         if (s != null && s.trim().isEmpty()) {
            y += 10 * (i - 1) + 1;
            break;
         }
      }

      return y;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"appleskin"};
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
