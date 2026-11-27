package com.bloodnbonesgaming.loadingscreens.client.gui;

import com.bloodnbonesgaming.lib.util.script.ScriptCall;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class GuiElementTextureTile extends GuiElementTexture {
   private double tileXSize;
   private double tileYSize;

   public GuiElementTextureTile(EnumGuiLocation location, ResourceLocation texture) {
      super(location, texture);
      this.tileXSize = (double)this.imageWidth;
      this.tileYSize = (double)this.imageHeight;
   }

   public void setTileSize(double x, double y) {
      this.tileXSize = x;
      this.tileYSize = y;
   }

   protected void drawTexture(int x, int y, int texWidth, int texHeight, int width, int height, double texStartX, double texStartY, double texEndX, double texEndY) {
      double xStartPos = 0.0D;
      double yStartPos = 0.0D;
      double xEndPos = (double)width / this.tileXSize * 2.0D;
      double yEndPos = (double)height / this.tileYSize * 2.0D;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer bufferbuilder = tessellator.getBuffer();
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      bufferbuilder.pos((double)(x + 0), (double)(y + height), 0.0D).tex(xStartPos, yEndPos).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + height), 0.0D).tex(xEndPos, yEndPos).endVertex();
      bufferbuilder.pos((double)(x + width), (double)(y + 0), 0.0D).tex(xEndPos, yStartPos).endVertex();
      bufferbuilder.pos((double)(x + 0), (double)(y + 0), 0.0D).tex(xStartPos, yStartPos).endVertex();
      tessellator.draw();
   }

   public Object call(String method, Object[] args) throws Exception {
      if (method.equals("setTileSize") && args.length == 2 && ScriptCall.isNumber(args[0]) && ScriptCall.isNumber(args[1])) {
         this.setTileSize(ScriptCall.toDouble(args[0]), ScriptCall.toDouble(args[1]));
         return null;
      }

      return super.call(method, args);
   }
}
