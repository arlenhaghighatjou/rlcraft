package noppes.vc.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import noppes.vc.blocks.tiles.TileShelf;
import noppes.vc.client.models.ModelShelf;

public class BlockShelfRenderer extends BlockRendererBasic {
   private final ModelShelf model = new ModelShelf();
   private int meta;

   public BlockShelfRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileShelf tile = (TileShelf)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      boolean drawLeft = true;
      boolean drawRight = true;
      if (var1 != null && var1.getPos() != BlockPos.ORIGIN) {
         BlockPos pos = var1.getPos();
         if (tile.rotation == 3) {
            drawLeft = this.shouldDraw(var1.getWorld(), pos.south(), 3);
            drawRight = this.shouldDraw(var1.getWorld(), pos.north(), 3);
         } else if (tile.rotation == 1) {
            drawLeft = this.shouldDraw(var1.getWorld(), pos.north(), 1);
            drawRight = this.shouldDraw(var1.getWorld(), pos.south(), 1);
         } else if (tile.rotation == 0) {
            drawLeft = this.shouldDraw(var1.getWorld(), pos.east(), 0);
            drawRight = this.shouldDraw(var1.getWorld(), pos.west(), 0);
         } else if (tile.rotation == 2) {
            drawLeft = this.shouldDraw(var1.getWorld(), pos.west(), 2);
            drawRight = this.shouldDraw(var1.getWorld(), pos.east(), 2);
         }
      }

      this.model.SupportLeft1.showModel = this.model.SupportLeft2.showModel = drawLeft;
      this.model.SupportRight1.showModel = this.model.SupportRight2.showModel = drawRight;
      this.setWood(tile, this.meta, 90);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }

   private boolean shouldDraw(World world, BlockPos pos, int rotation) {
      TileEntity tile = world.getTileEntity(pos);
      if (tile != null && tile instanceof TileShelf) {
         return ((TileShelf)tile).rotation != rotation;
      } else {
         return true;
      }
   }
}
