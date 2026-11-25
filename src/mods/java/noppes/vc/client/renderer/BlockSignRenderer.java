package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import noppes.vc.blocks.tiles.TileSign;
import noppes.vc.client.models.ModelSign;

public class BlockSignRenderer extends BlockRendererBasic {
   private final ModelSign model = new ModelSign();
   private int meta;

   public BlockSignRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileSign tile = (TileSign)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.62F, (float)var6 + 0.5F);
      this.setWood(tile, this.meta, 90);
      this.model.Sign.render(0.0625F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(Steel);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      if (tile != null && tile.icon != null && !this.playerTooFar(tile)) {
         this.doRender(var2, var4, var6, tile.rotation, tile.icon);
      }

      GlStateManager.popMatrix();
   }

   public void doRender(double par2, double par4, double par6, int meta, ItemStack iicon) {
      if (!iicon.isEmpty() && !(iicon.getItem() instanceof ItemBlock)) {
         GlStateManager.pushMatrix();
         this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         GlStateManager.translate(0.0D, 1.0199999809265137D, -3.57D);
         GlStateManager.depthMask(false);
         float f2 = 0.024F;
         Minecraft mc = Minecraft.getMinecraft();
         GlStateManager.scale(-f2, f2, f2);
         mc.getRenderItem().zLevel = 0.0F;
         mc.getRenderItem().renderItemAndEffectIntoGUI(iicon, -8, -8);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0D, 0.0D, -3.57D / (double)f2 * 2.0D);
         mc.getRenderItem().zLevel = 0.0F;
         mc.getRenderItem().renderItemAndEffectIntoGUI(iicon, -8, -8);
         GlStateManager.depthMask(true);
         GlStateManager.popMatrix();
      }
   }
}
