package net.blay09.mods.waystones.client.gui;

import net.blay09.mods.waystones.util.WaystoneEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.config.GuiButtonExt;

public class GuiButtonRemoveWaystone extends GuiButtonExt {
   public static boolean shiftGuard;
   private static final ResourceLocation BEACON = new ResourceLocation("textures/gui/container/beacon.png");
   private final GuiButtonWaystoneEntry parentButton;

   public GuiButtonRemoveWaystone(int id, int x, int y, GuiButtonWaystoneEntry parentButton) {
      super(id, x, y, "");
      this.width = 13;
      this.height = 13;
      this.parentButton = parentButton;
   }

   public WaystoneEntry getWaystone() {
      return this.parentButton.getWaystone();
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      if (super.mousePressed(mc, mouseX, mouseY)) {
         shiftGuard = true;
         return true;
      } else {
         return false;
      }
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
      this.visible = shiftGuard != GuiScreen.isShiftKeyDown();
      if (this.visible) {
         shiftGuard = false;
      }

      if (this.visible && mouseY >= this.parentButton.y && mouseY < this.parentButton.y + this.parentButton.height) {
         super.drawButton(mc, mouseX, mouseY, partial);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         mc.getTextureManager().bindTexture(BEACON);
         Gui.drawScaledCustomSizeModalRect(this.x + 3, this.y + 3, 114.0F, 223.0F, 13, 13, 7, 7, 256.0F, 256.0F);
      }

   }
}
