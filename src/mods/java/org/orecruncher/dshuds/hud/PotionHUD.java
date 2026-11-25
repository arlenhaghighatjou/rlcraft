package org.orecruncher.dshuds.hud;

import com.google.common.collect.Ordering;
import java.util.Collection;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dshuds.ModOptions;
import org.orecruncher.lib.Color;
import org.orecruncher.lib.Localization;
import org.orecruncher.lib.collections.ObjectArray;

@SideOnly(Side.CLIENT)
public class PotionHUD extends GuiOverlay {
   private static final Color TEXT_POTION_NAME = Color.MC_WHITE;
   private static final Color TEXT_POTION_NAME_BAD = Color.MC_RED;
   private static final Color TEXT_POTION_NAME_AMBIENT = Color.MC_GOLD;
   private static final Color TEXT_POTION_DURATION = Color.MC_GREEN;
   private static final Color TEXT_POTION_DURATION_LOW = Color.MC_RED;
   private static final Color TEXT_POTION_DURATION_LOW_DARK = Color.MC_DARKRED;
   protected final ObjectArray<PotionHUD.PotionInfo> potions = new ObjectArray();

   public PotionHUD() {
   }

   private boolean skipDisplay(@Nonnull PotionEffect effect) {
      Potion potion = effect.getPotion();
      return potion == null || !potion.shouldRenderHUD(effect) || !potion.shouldRenderInvText(effect);
   }

   private boolean f3Active() {
      return Minecraft.getMinecraft().gameSettings.showDebugInfo;
   }

   public void doTick(int tickRef) {
      this.potions.clear();
      if (ModOptions.potionHUD.enabled && !ModOptions.potionHUD.none) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         if (player != null) {
            Collection<PotionEffect> collection = player.getActivePotionEffects();
            if (collection != null && !collection.isEmpty()) {
               for(PotionEffect effect : Ordering.natural().reverse().sortedCopy(collection)) {
                  if (!this.skipDisplay(effect)) {
                     this.potions.add(new PotionHUD.PotionInfo(effect));
                  }
               }

            }
         }
      }
   }

   public void doRender(Pre event) {
      if (ModOptions.potionHUD.none && event.getType() == ElementType.POTION_ICONS) {
         event.setCanceled(true);
      } else if (event.getType() == ElementType.POTION_ICONS && this.potions.size() != 0) {
         event.setCanceled(true);
         if (!this.f3Active()) {
            ScaledResolution resolution = event.getResolution();
            float GUITOP = (float)ModOptions.potionHUD.topOffset;
            float GUILEFT = ModOptions.potionHUD.anchor == 0 ? (float)ModOptions.potionHUD.leftOffset : (float)(resolution.getScaledWidth() - ModOptions.potionHUD.leftOffset) - 120.0F * ModOptions.potionHUD.scale;
            float SCALE = ModOptions.potionHUD.scale;
            Minecraft mc = Minecraft.getMinecraft();
            FontRenderer font = mc.fontRenderer;
            int guiLeft = 2;
            int guiTop = 2;
            GlStateManager.pushMatrix();
            GlStateManager.translate(GUILEFT, GUITOP, 0.0F);
            GlStateManager.scale(SCALE, SCALE, SCALE);
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            int k = 33;
            if (this.potions.size() > 7) {
               k = 198 / (this.potions.size() - 1);
            }

            for(PotionHUD.PotionInfo potion : this.potions) {
               mc.getTextureManager().bindTexture(GuiContainer.INVENTORY_BACKGROUND);
               GlStateManager.color(1.0F, 1.0F, 1.0F, ModOptions.potionHUD.transparency);
               this.drawTexturedModalRect(2, guiTop, 0, 166, 140, 32);
               if (potion.hasStatusIcon()) {
                  int l = potion.getStatusIconIndex();
                  this.drawTexturedModalRect(8, guiTop + 7, 0 + l % 8 * 18, 198 + l / 8 * 18, 18, 18);
               } else {
                  try {
                     potion.getPotion().renderHUDEffect(5, guiTop + 4, potion.getPotionEffect(), mc, 1.0F);
                  } catch (Exception var16) {
                     ;
                  }
               }

               try {
                  potion.getPotion().renderInventoryEffect(2, guiTop, potion.getPotionEffect(), mc);
               } catch (Exception var15) {
                  ;
               }

               String potionText = potion.getEffectText();
               String durationText = potion.getDurationText();
               if (potionText != null) {
                  font.drawStringWithShadow(potionText, 30.0F, (float)(guiTop + 6), potion.getEffectColor());
                  font.drawStringWithShadow(durationText, 30.0F, (float)(guiTop + 6 + 10), potion.getDurationColor());
               }

               guiTop += k;
            }

            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }
   }

   private static class PotionInfo {
      private final PotionEffect potionEffect;
      private final Potion potion;
      private final int effectColor;
      private final String effectText;
      private final int durationColor;
      private final String durationText;

      public PotionInfo(@Nonnull PotionEffect potion) {
         this.potionEffect = potion;
         this.potion = this.potionEffect.getPotion();
         if (this.potion.shouldRenderInvText(this.potionEffect)) {
            String s1 = Localization.format(this.potion.getName(), new Object[0]);
            if (this.potionEffect.getAmplifier() == 1) {
               s1 = s1 + " " + Localization.format("enchantment.level.2", new Object[0]);
            } else if (this.potionEffect.getAmplifier() == 2) {
               s1 = s1 + " " + Localization.format("enchantment.level.3", new Object[0]);
            } else if (this.potionEffect.getAmplifier() == 3) {
               s1 = s1 + " " + Localization.format("enchantment.level.4", new Object[0]);
            }

            this.effectText = s1;
            this.effectColor = (this.potion.isBadEffect() ? PotionHUD.TEXT_POTION_NAME_BAD : (this.potionEffect.getIsAmbient() ? PotionHUD.TEXT_POTION_NAME_AMBIENT : PotionHUD.TEXT_POTION_NAME)).rgbWithAlpha(ModOptions.potionHUD.transparency);
            int threshold = this.potionEffect.getIsAmbient() ? 170 : 200;
            int duration = this.potionEffect.getDuration();
            this.durationColor = (duration <= threshold ? ((duration / 10 & 1) != 0 ? PotionHUD.TEXT_POTION_DURATION_LOW_DARK : PotionHUD.TEXT_POTION_DURATION_LOW) : PotionHUD.TEXT_POTION_DURATION).rgbWithAlpha(ModOptions.potionHUD.transparency);
            this.durationText = Potion.getPotionDurationString(this.potionEffect, 1.0F);
         } else {
            this.effectText = null;
            this.durationText = null;
            this.durationColor = -1;
            this.effectColor = -1;
         }

      }

      public PotionEffect getPotionEffect() {
         return this.potionEffect;
      }

      public Potion getPotion() {
         return this.potion;
      }

      public boolean hasStatusIcon() {
         return this.potion.hasStatusIcon();
      }

      public int getStatusIconIndex() {
         return this.potion.getStatusIconIndex();
      }

      public String getEffectText() {
         return this.effectText;
      }

      public int getEffectColor() {
         return this.effectColor;
      }

      public String getDurationText() {
         return this.durationText;
      }

      public int getDurationColor() {
         return this.durationColor;
      }
   }
}
