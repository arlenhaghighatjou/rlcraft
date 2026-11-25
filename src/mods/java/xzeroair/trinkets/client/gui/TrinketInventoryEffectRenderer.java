package xzeroair.trinkets.client.gui;

import com.google.common.collect.Ordering;
import java.util.Collection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.GuiScreenEvent.PotionShiftEvent;
import net.minecraftforge.common.MinecraftForge;
import xzeroair.trinkets.util.TrinketsConfig;

public class TrinketInventoryEffectRenderer extends TrinketGuiContainer {
   protected boolean hasActivePotionEffects;

   public TrinketInventoryEffectRenderer(Container inventorySlotsIn) {
      super(inventorySlotsIn);
   }

   public void initGui() {
      super.initGui();
      this.updateActivePotionEffects();
   }

   protected void updateActivePotionEffects() {
      boolean hasVisibleEffect = false;

      for(PotionEffect potioneffect : this.mc.player.getActivePotionEffects()) {
         Potion potion = potioneffect.getPotion();
         if (potion.shouldRender(potioneffect)) {
            hasVisibleEffect = true;
            break;
         }
      }

      if (!this.mc.player.getActivePotionEffects().isEmpty() && hasVisibleEffect) {
         if (MinecraftForge.EVENT_BUS.post(new PotionShiftEvent(this))) {
            this.guiLeft = (this.width - this.xSize) / 2;
         } else {
            this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
         }

         this.hasActivePotionEffects = true;
      } else {
         this.guiLeft = (this.width - this.xSize) / 2;
         this.hasActivePotionEffects = false;
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.hasActivePotionEffects) {
         this.drawActivePotionEffects();
      }

   }

   private void drawActivePotionEffects() {
      int xPos = TrinketsConfig.CLIENT.GUI.X;
      int yPos = TrinketsConfig.CLIENT.GUI.Y;
      int slots = TrinketsConfig.SERVER.GUI.guiSlotsRows;
      int multi = MathHelper.ceil((double)slots / 8.0D);
      int offset = 18 * multi;
      int i = this.guiLeft - xPos + 5 - 140 - offset;
      int j = this.guiTop;
      int k = 166;
      Collection<PotionEffect> collection = this.mc.player.getActivePotionEffects();
      if (!collection.isEmpty()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         int l = 33;
         if (collection.size() > 5) {
            l = 132 / (collection.size() - 1);
         }

         for(PotionEffect potioneffect : Ordering.natural().sortedCopy(collection)) {
            Potion potion = potioneffect.getPotion();
            if (potion.shouldRender(potioneffect)) {
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               this.mc.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
               this.drawTexturedModalRect(i, j, 0, 166, 140, 32);
               if (potion.hasStatusIcon()) {
                  int i1 = potion.getStatusIconIndex();
                  this.drawTexturedModalRect(i + 6, j + 7, 0 + i1 % 8 * 18, 198 + i1 / 8 * 18, 18, 18);
               }

               potion.renderInventoryEffect(potioneffect, this, i, j, this.zLevel);
               if (!potion.shouldRenderInvText(potioneffect)) {
                  j += l;
               } else {
                  String s1 = I18n.format(potion.getName(), new Object[0]);
                  if (potioneffect.getAmplifier() == 1) {
                     s1 = s1 + " " + I18n.format("enchantment.level.2", new Object[0]);
                  } else if (potioneffect.getAmplifier() == 2) {
                     s1 = s1 + " " + I18n.format("enchantment.level.3", new Object[0]);
                  } else if (potioneffect.getAmplifier() == 3) {
                     s1 = s1 + " " + I18n.format("enchantment.level.4", new Object[0]);
                  }

                  this.fontRenderer.drawStringWithShadow(s1, (float)(i + 10 + 18), (float)(j + 6), 16777215);
                  String s = Potion.getPotionDurationString(potioneffect, 1.0F);
                  this.fontRenderer.drawStringWithShadow(s, (float)(i + 10 + 18), (float)(j + 6 + 10), 8355711);
                  j += l;
               }
            }
         }
      }

   }
}
