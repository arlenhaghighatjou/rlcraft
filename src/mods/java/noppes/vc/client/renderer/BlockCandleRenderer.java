package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.blocks.tiles.TileCandle;
import noppes.vc.client.models.ModelCandle;
import noppes.vc.client.models.ModelCandleCeiling;
import noppes.vc.client.models.ModelCandleWall;

public class BlockCandleRenderer extends TileEntitySpecialRenderer {
   private final ModelCandle model = new ModelCandle();
   private final ModelCandleWall modelWall = new ModelCandleWall();
   private final ModelCandleCeiling modelCeiling = new ModelCandleCeiling();
   private static final ResourceLocation resource1 = new ResourceLocation("variedcommodities", "textures/models/block/candle.png");

   public BlockCandleRenderer() {
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileCandle tile = (TileCandle)var1;
      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      if (tile != null) {
         GlStateManager.rotate((float)(45 * tile.rotation), 0.0F, 1.0F, 0.0F);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(resource1);
      if (tile != null && tile.color != 0) {
         if (tile.color == 1) {
            this.modelCeiling.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         } else {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            this.modelWall.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         }
      } else {
         this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      }

      GlStateManager.popMatrix();
   }
}
