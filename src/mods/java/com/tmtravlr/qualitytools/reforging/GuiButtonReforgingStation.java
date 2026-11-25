package com.tmtravlr.qualitytools.reforging;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonReforgingStation extends GuiButton {
   protected static final ResourceLocation REFORGE_BUTTON_TEXTURES = new ResourceLocation("qualitytools", "textures/gui/container/reforging_station.png");
   private boolean pressed = false;
   private int baseX;
   private int baseY;

   public GuiButtonReforgingStation(int id, int posX, int posY) {
      super(id, posX, posY, 16, 16, "");
      this.baseX = posX;
      this.baseY = posY;
   }

   public void updatePos(int x, int y) {
      this.x = this.baseX + x;
      this.y = this.baseY + y;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
      if (this.visible && this.enabled) {
         mc.getTextureManager().bindTexture(REFORGE_BUTTON_TEXTURES);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         boolean hovering = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
         GuiButtonReforgingStation.Icon icon;
         if (this.pressed) {
            icon = GuiButtonReforgingStation.Icon.PRESSED;
         } else if (hovering) {
            icon = GuiButtonReforgingStation.Icon.HOVER;
         } else {
            icon = GuiButtonReforgingStation.Icon.NOMRAL;
         }

         this.drawTexturedModalRect(this.x, this.y, icon.getX(), icon.getY(), this.width, this.height);
      }

   }

   public void playPressSound(SoundHandler soundHandlerIn) {
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      this.pressed = super.mousePressed(mc, mouseX, mouseY);
      return this.pressed;
   }

   public void mouseReleased(int mouseX, int mouseY) {
      this.pressed = false;
   }

   @SideOnly(Side.CLIENT)
   static enum Icon {
      NOMRAL(176, 0),
      HOVER(176, 16),
      PRESSED(176, 32);

      private final int x;
      private final int y;

      private Icon(int xToSet, int yToSet) {
         this.x = xToSet;
         this.y = yToSet;
      }

      public int getX() {
         return this.x;
      }

      public int getY() {
         return this.y;
      }
   }
}
