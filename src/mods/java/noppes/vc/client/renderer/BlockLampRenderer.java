package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.blocks.tiles.TileLamp;
import noppes.vc.client.models.ModelLamp;
import noppes.vc.client.models.ModelLampCeiling;
import noppes.vc.client.models.ModelLampWall;

public class BlockLampRenderer extends TileEntitySpecialRenderer {
   private final ModelLamp model = new ModelLamp();
   private final ModelLampCeiling model2 = new ModelLampCeiling();
   private final ModelLampWall model3 = new ModelLampWall();
   private static final ResourceLocation resource1 = new ResourceLocation("variedcommodities", "textures/models/block/lamp.png");

   public BlockLampRenderer() {
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileLamp tile = (TileLamp)var1;
      GlStateManager.enableLighting();
      GlStateManager.pushMatrix();
      GlStateManager.disableBlend();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      if (tile != null) {
         GlStateManager.rotate((float)(45 * tile.rotation), 0.0F, 1.0F, 0.0F);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(resource1);
      if (tile != null && tile.color != 0) {
         if (tile.color == 1) {
            this.model2.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         } else {
            this.model3.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         }
      } else {
         this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      }

      GlStateManager.popMatrix();
   }
}
