package xzeroair.trinkets.client.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.client.gui.hud.mana.ManaGui;
import xzeroair.trinkets.client.gui.hud.mana.ManaHud;
import xzeroair.trinkets.util.TrinketsConfig;

public class ScreenOverlayEvents {
   public static ScreenOverlayEvents instance = new ScreenOverlayEvents();
   private final Minecraft mc = Minecraft.getMinecraft();
   private final ManaGui manaGui;
   private float mana;
   private float maxMana;
   private float manaCost;
   private int updateCounter;

   public ScreenOverlayEvents() {
      this.manaGui = new ManaGui(this.mc);
      this.mana = 0.0F;
      this.maxMana = 0.0F;
      this.manaCost = 0.0F;
      this.updateCounter = 0;
   }

   public void SyncMana(float mana, int bonusMana, float maxMana) {
      this.setMana(mana);
      this.setMaxMana(maxMana);
   }

   public void SyncCost(float cost) {
      this.setCost(cost);
   }

   @SubscribeEvent
   public void renderGameOverlayPostEvent(Post event) {
      if (!event.isCanceled() && event.getType() == ElementType.ALL && TrinketsConfig.CLIENT.MPBar.shown && TrinketsConfig.SERVER.mana.mana_enabled) {
         MagicStats stats = Capabilities.getMagicStats(Minecraft.getMinecraft().player);
         if (stats != null) {
            this.setMana(stats.getMana());
            this.setMaxMana(stats.getMaxMana());
         }

         if (!this.needMana() && this.getCost() <= 0.0F) {
            if (this.updateCounter() && !(Minecraft.getMinecraft().currentScreen instanceof ManaHud) && !TrinketsConfig.CLIENT.MPBar.always_shown) {
               return;
            }
         } else {
            this.resetCounter();
         }

         int h = event.getResolution().getScaledHeight();
         int w = event.getResolution().getScaledWidth();
         double x = TrinketsConfig.CLIENT.MPBar.translatedX;
         double y = TrinketsConfig.CLIENT.MPBar.translatedY;
         int xPos = (int)Math.round((double)w * x);
         int yPos = (int)Math.round((double)h * y);
         GlStateManager.pushMatrix();
         this.manaGui.renderManaGui(event, MathHelper.clamp(xPos, 0, w), MathHelper.clamp(yPos, 0, h), this.updateCounter, this.getMana(), this.getMaxMana(), this.getCost());
         GlStateManager.popMatrix();
      }
   }

   private boolean updateCounter() {
      if (this.updateCounter < 80) {
         ++this.updateCounter;
      }

      return this.updateCounter >= 80;
   }

   public int getUpdateCounter() {
      return this.updateCounter;
   }

   private void resetCounter() {
      this.updateCounter = 0;
   }

   private void setMana(float mana) {
      if (this.mana != mana) {
         this.mana = mana;
         this.resetCounter();
      }

   }

   private void setMaxMana(float maxMana) {
      if (this.maxMana != maxMana) {
         this.maxMana = maxMana;
         this.resetCounter();
      }

   }

   private boolean needMana() {
      return this.getMana() != this.getMaxMana();
   }

   private float getMana() {
      return this.mana;
   }

   private float getMaxMana() {
      return this.maxMana;
   }

   private float getCost() {
      return this.manaCost;
   }

   private void setCost(float cost) {
      this.manaCost = cost;
   }
}
