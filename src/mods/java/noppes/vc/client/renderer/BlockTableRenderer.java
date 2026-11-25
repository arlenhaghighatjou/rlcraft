package noppes.vc.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import noppes.vc.VCBlocks;
import noppes.vc.blocks.tiles.TileTable;
import noppes.vc.client.models.ModelTable;

public class BlockTableRenderer extends BlockRendererBasic {
   private final ModelTable model = new ModelTable();
   private int meta;

   public BlockTableRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileTable tile = (TileTable)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      if (var1 != null && var1.getPos() != BlockPos.ORIGIN) {
         BlockPos pos = var1.getPos();
         boolean south = var1.getWorld().getBlockState(pos.east()).getBlock() == VCBlocks.table;
         boolean north = var1.getWorld().getBlockState(pos.west()).getBlock() == VCBlocks.table;
         boolean east = var1.getWorld().getBlockState(pos.south()).getBlock() == VCBlocks.table;
         boolean west = var1.getWorld().getBlockState(pos.north()).getBlock() == VCBlocks.table;
         this.model.Shape1.showModel = !south && !east;
         this.model.Shape3.showModel = !north && !west;
         this.model.Shape4.showModel = !north && !east;
         this.model.Shape5.showModel = !south && !west;
      } else {
         this.model.Shape1.showModel = this.model.Shape3.showModel = this.model.Shape4.showModel = this.model.Shape5.showModel = true;
      }

      this.setWood(tile, this.meta, 0);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      if (tile != null) {
         GlStateManager.rotate((float)(90 * tile.rotation), 0.0F, 1.0F, 0.0F);
      }

      this.model.Table.render(0.0625F);
      GlStateManager.popMatrix();
   }
}
