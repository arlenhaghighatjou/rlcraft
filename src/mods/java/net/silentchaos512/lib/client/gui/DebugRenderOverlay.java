package net.silentchaos512.lib.client.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public abstract class DebugRenderOverlay extends Gui {
   protected static final String SPLITTER = "=";
   private static final int DEFAULT_UPDATE_FREQUENCY = 10;
   private static final int DEFAULT_SPLIT_WIDTH = 100;
   private List<String> debugText = new ArrayList();
   private int startX = 3;
   private int startY = 3;
   private int ticksPassed = 0;

   protected DebugRenderOverlay() {
      MinecraftForge.EVENT_BUS.register(this);
   }

   protected DebugRenderOverlay(int startX, int startY) {
      MinecraftForge.EVENT_BUS.register(this);
      this.startX = startX;
      this.startY = startY;
   }

   @Nonnull
   public abstract List<String> getDebugText();

   public abstract float getTextScale();

   @Nonnegative
   public int getUpdateFrequency() {
      return 10;
   }

   @Nonnegative
   public int getSplitWidth() {
      return 100;
   }

   public abstract boolean isHidden();

   protected void drawLine(FontRenderer font, String line, int x, int y, int color) {
      String[] array = line.split("=");
      if (array.length == 2) {
         font.drawStringWithShadow(array[0].trim(), (float)x, (float)y, color);
         font.drawStringWithShadow(array[1].trim(), (float)(x + this.getSplitWidth()), (float)y, color);
      } else {
         font.drawStringWithShadow(line, (float)x, (float)y, color);
      }

   }

   public int getStartX() {
      return this.startX;
   }

   public int getStartY() {
      return this.startY;
   }

   @SubscribeEvent
   public void renderTick(Post event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (!this.isHidden() && !this.debugText.isEmpty() && !mc.isGamePaused() && !mc.gameSettings.showDebugInfo && event.getType() == ElementType.ALL) {
         float scale = this.getTextScale();
         if (scale > 0.0F) {
            FontRenderer font = Minecraft.getMinecraft().fontRenderer;
            GlStateManager.pushMatrix();
            GlStateManager.scale(scale, scale, 1.0F);
            int x = this.getStartX();
            int y = this.getStartY();

            for(String line : this.debugText) {
               this.drawLine(font, line, x, y, 16777215);
               y += 10;
            }

            GlStateManager.popMatrix();
         }
      }
   }

   @SubscribeEvent
   public void clientTick(ClientTickEvent event) {
      if (event.phase != Phase.START) {
         if (this.getUpdateFrequency() == 0 || ++this.ticksPassed % this.getUpdateFrequency() == 0) {
            this.debugText = this.getDebugText();
         }

      }
   }
}
