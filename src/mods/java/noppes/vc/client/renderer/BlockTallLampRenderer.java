package noppes.vc.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.blocks.tiles.TileTallLamp;
import noppes.vc.client.models.ModelTallLamp;
import noppes.vc.client.models.ModelTallLampTop;

public class BlockTallLampRenderer extends BlockRendererBasic {
   private final ModelTallLamp model = new ModelTallLamp();
   private final ModelTallLampTop top = new ModelTallLampTop();
   public static final ResourceLocation resourceTop = new ResourceLocation("variedcommodities", "textures/cache/wool_colored_white.png");
   private int meta;

   public BlockTallLampRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileTallLamp tile = (TileTallLamp)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      if (tile == null) {
         GlStateManager.translate(0.0D, -0.5D, 0.0D);
         GlStateManager.scale(1.0D, 0.7D, 1.0D);
      }

      this.setMaterial(tile, this.meta);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      this.bindTexture(resourceTop);
      this.setColor(tile, this.meta);
      this.top.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
