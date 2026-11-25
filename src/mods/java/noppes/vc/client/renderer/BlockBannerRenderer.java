package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import noppes.vc.blocks.tiles.TileBanner;
import noppes.vc.client.models.ModelBanner;
import noppes.vc.client.models.ModelBannerFlag;

public class BlockBannerRenderer extends BlockRendererBasic {
   private final ModelBanner model = new ModelBanner();
   private final ModelBannerFlag flag = new ModelBannerFlag();
   public static final ResourceLocation resourceFlag = new ResourceLocation("variedcommodities", "textures/models/block/banner_flag.png");
   private int meta;

   public BlockBannerRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileBanner tile = (TileBanner)var1;
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      if (tile == null) {
         GlStateManager.translate(0.0D, -0.5D, 0.0D);
         GlStateManager.scale(1.0D, 0.7D, 1.0D);
      }

      this.setMaterial(tile, this.meta);
      this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      this.bindTexture(resourceFlag);
      this.setColor(tile, this.meta);
      this.flag.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
      if (tile != null && tile.icon != null && !this.playerTooFar(tile)) {
         this.doRender(var2, var4, var6, tile.rotation, tile.icon);
      }

   }

   public void doRender(double par2, double par4, double par6, int meta, ItemStack iicon) {
      if (!iicon.isEmpty() && !(iicon.getItem() instanceof ItemBlock)) {
         GlStateManager.pushMatrix();
         this.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
         GlStateManager.translate((float)par2 + 0.5F, (float)par4 + 1.3F, (float)par6 + 0.5F);
         GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate((float)(90 * meta), 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0D, 0.0D, 0.62D);
         GlStateManager.depthMask(false);
         float f2 = 0.05F;
         Minecraft mc = Minecraft.getMinecraft();
         GlStateManager.scale((double)f2, (double)f2, -0.005D);
         mc.getRenderItem().zLevel = 0.0F;
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         mc.getRenderItem().renderItemAndEffectIntoGUI(iicon, -8, -8);
         GlStateManager.depthMask(true);
         GlStateManager.popMatrix();
      }
   }

   public int specialRenderDistance() {
      return 26;
   }
}
