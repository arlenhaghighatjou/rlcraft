package kenkron.antiqueatlasoverlay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

class SetTileRenderer {
   private final HashMap<ResourceLocation, ArrayList<SetTileRenderer.TileCorner>> subjects = new HashMap();
   private int tileHalfSize = 8;

   public SetTileRenderer(int tileHalfSize) {
      this.tileHalfSize = tileHalfSize;
   }

   public void addTileCorner(ResourceLocation texture, int x, int y, int u, int v) {
      ArrayList<SetTileRenderer.TileCorner> set = (ArrayList)this.subjects.computeIfAbsent(texture, (k) -> {
         return new ArrayList();
      });
      set.add(new SetTileRenderer.TileCorner(x, y, u, v));
   }

   public void draw() {
      for(ResourceLocation key : this.subjects.keySet()) {
         ArrayList<SetTileRenderer.TileCorner> tca = (ArrayList)this.subjects.get(key);
         Minecraft.getMinecraft().renderEngine.bindTexture(key);
         Tessellator tessellator = Tessellator.getInstance();
         BufferBuilder renderer = tessellator.getBuffer();
         renderer.begin(7, DefaultVertexFormats.POSITION_TEX);

         for(SetTileRenderer.TileCorner tc : tca) {
            this.drawInlineAutotileCorner(tc.x, tc.y, tc.u, tc.v);
         }

         tessellator.draw();
      }

   }

   private void drawInlineAutotileCorner(int x, int y, int u, int v) {
      float minU = (float)u / 4.0F;
      float maxU = (float)(u + 1) / 4.0F;
      float minV = (float)v / 6.0F;
      float maxV = (float)(v + 1) / 6.0F;
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder renderer = tessellator.getBuffer();
      renderer.pos((double)(x + this.tileHalfSize), (double)(y + this.tileHalfSize), 0.0D).tex((double)maxU, (double)maxV).endVertex();
      renderer.pos((double)(x + this.tileHalfSize), (double)y, 0.0D).tex((double)maxU, (double)minV).endVertex();
      renderer.pos((double)x, (double)y, 0.0D).tex((double)minU, (double)minV).endVertex();
      renderer.pos((double)x, (double)(y + this.tileHalfSize), 0.0D).tex((double)minU, (double)maxV).endVertex();
   }

   public class TileCorner {
      final int x;
      final int y;
      final int u;
      final int v;

      TileCorner(int x, int y, int u, int v) {
         this.x = x;
         this.y = y;
         this.u = u;
         this.v = v;
      }
   }
}
