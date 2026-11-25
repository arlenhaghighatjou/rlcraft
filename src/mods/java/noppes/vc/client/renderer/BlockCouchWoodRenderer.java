package noppes.vc.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import noppes.vc.blocks.tiles.TileCouchWood;
import noppes.vc.client.models.ModelCouchWoodLeft;
import noppes.vc.client.models.ModelCouchWoodMiddle;
import noppes.vc.client.models.ModelCouchWoodRight;
import noppes.vc.client.models.ModelCouchWoodSingle;

public class BlockCouchWoodRenderer extends BlockRendererBasic {
   private final ModelBase model = new ModelCouchWoodMiddle();
   private final ModelBase modelLeft = new ModelCouchWoodLeft();
   private final ModelBase modelRight = new ModelCouchWoodRight();
   private final ModelBase modelCorner = new ModelCouchWoodSingle();
   private int meta;

   public BlockCouchWoodRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileCouchWood tile = (TileCouchWood)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      this.setWood(tile, this.meta, 90);
      if (tile != null && (!tile.hasLeft || !tile.hasRight)) {
         if (tile.hasLeft) {
            this.modelLeft.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         } else if (tile.hasRight) {
            this.modelRight.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         } else {
            this.modelCorner.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         }
      } else {
         this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      }

      GlStateManager.popMatrix();
   }
}
