package me.ichun.mods.ichunutil.client.gui.window.element;

import me.ichun.mods.ichunutil.client.gui.window.Window;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class ElementButtonTextured extends ElementButton {
   public ResourceLocation txLocation;

   public ElementButtonTextured(Window window, int x, int y, int w, int h, int ID, boolean igMin, int sideH, int sideV, String title, ResourceLocation loc) {
      super(window, x, y, w, h, ID, igMin, sideH, sideV, title);
      this.txLocation = loc;
   }

   public ElementButtonTextured(Window window, int x, int y, int ID, boolean igMin, int sideH, int sideV, String title, ResourceLocation loc) {
      super(window, x, y, 20, 20, ID, igMin, sideH, sideV, title);
      this.txLocation = loc;
   }

   public void draw(int mouseX, int mouseY, boolean hover) {
      super.draw(mouseX, mouseY, hover);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RendererHelper.drawTextureOnScreen(this.txLocation, (double)(this.getPosX() + 2), (double)(this.getPosY() + 2), (double)(this.width - 4), (double)(this.height - 4), 0.0D);
   }

   public String tooltip() {
      return this.text;
   }
}
