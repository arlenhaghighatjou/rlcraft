package noppes.vc.client.renderer;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import noppes.vc.blocks.tiles.TileCouchWool;
import noppes.vc.client.models.ModelCouchCorner;
import noppes.vc.client.models.ModelCouchCornerWool;
import noppes.vc.client.models.ModelCouchLeft;
import noppes.vc.client.models.ModelCouchLeftWool;
import noppes.vc.client.models.ModelCouchMiddle;
import noppes.vc.client.models.ModelCouchMiddleWool;
import noppes.vc.client.models.ModelCouchRight;
import noppes.vc.client.models.ModelCouchRightWool;

public class BlockCouchWoolRenderer extends BlockRendererBasic {
   private final ModelBase model = new ModelCouchMiddle();
   private final ModelBase model2 = new ModelCouchMiddleWool();
   private final ModelBase modelLeft = new ModelCouchLeft();
   private final ModelBase modelLeft2 = new ModelCouchLeftWool();
   private final ModelBase modelRight = new ModelCouchRight();
   private final ModelBase modelRight2 = new ModelCouchRightWool();
   private final ModelBase modelCorner = new ModelCouchCorner();
   private final ModelBase modelCorner2 = new ModelCouchCornerWool();
   private int meta;

   public BlockCouchWoolRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileCouchWool tile = (TileCouchWool)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      this.setWood(tile, this.meta, 90);
      if (tile == null) {
         this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else if (tile.hasCornerLeft) {
         this.modelCorner.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else if (tile.hasCornerRight) {
         GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
         this.modelCorner.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else if (tile.hasLeft && tile.hasRight) {
         this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else if (tile.hasLeft) {
         this.modelLeft.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else if (tile.hasRight) {
         this.modelRight.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else {
         this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      }

      this.bindTexture(BlockTallLampRenderer.resourceTop);
      this.setColor(tile, this.meta);
      if (tile == null) {
         this.model2.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else if (!tile.hasCornerLeft && !tile.hasCornerRight) {
         if (tile.hasLeft && tile.hasRight) {
            this.model2.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         } else if (tile.hasLeft) {
            this.modelLeft2.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         } else if (tile.hasRight) {
            this.modelRight2.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         } else {
            this.model2.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
         }
      } else {
         this.modelCorner2.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      }

      GlStateManager.popMatrix();
   }
}
