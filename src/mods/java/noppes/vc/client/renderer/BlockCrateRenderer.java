package noppes.vc.client.renderer;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import noppes.vc.blocks.tiles.TileCrate;
import noppes.vc.client.models.ModelCrate;

public class BlockCrateRenderer extends BlockRendererBasic {
   private final ModelCrate model = new ModelCrate();
   private int meta;

   public BlockCrateRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileCrate tile = (TileCrate)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      this.setWood(tile, this.meta, 90);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
