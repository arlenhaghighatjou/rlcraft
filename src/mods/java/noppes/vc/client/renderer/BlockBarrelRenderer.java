package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.blocks.tiles.TileBarrel;
import noppes.vc.client.models.ModelBarrel;
import noppes.vc.client.models.ModelBarrelLit;

public class BlockBarrelRenderer extends BlockRendererBasic {
   private final ModelBarrel model = new ModelBarrel();
   private final ModelBarrelLit modelLit = new ModelBarrelLit();
   private static final ResourceLocation resource1 = new ResourceLocation("variedcommodities", "textures/models/block/barrel.png");
   private int meta;

   public BlockBarrelRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileBarrel tile = (TileBarrel)var1;
      GlStateManager.enableAlpha();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.42F, (float)var6 + 0.5F);
      GlStateManager.scale(1.0F, 0.94F, 1.0F);
      GlStateManager.enableCull();
      this.setWood(tile, this.meta, 45);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(resource1);
      this.modelLit.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.disableCull();
      GlStateManager.popMatrix();
   }
}
