package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import noppes.vc.blocks.tiles.TileBigSign;
import noppes.vc.client.TextBlockClient;
import noppes.vc.client.models.ModelBigSign;

public class BlockBigSignRenderer extends BlockRendererBasic {
   private final ModelBigSign model = new ModelBigSign();
   private static final ResourceLocation resource = new ResourceLocation("variedcommodities", "textures/models/block/big_sign.png");

   public BlockBigSignRenderer() {
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileBigSign tile = (TileBigSign)var1;
      Minecraft mc = Minecraft.getMinecraft();
      GlStateManager.enableLighting();
      if (tile != null && (tile.block == null || tile.hasChanged)) {
         tile.block = new TextBlockClient(tile.getText(), 112, true, new Object[]{mc.player});
         tile.hasChanged = false;
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableBlend();
      float xOffset = 0.0F;
      float yOffset = 0.0F;
      if (tile != null && tile.rotation != 1) {
         if (tile.rotation == 3) {
            xOffset = 0.44F;
         } else if (tile.rotation == 2) {
            yOffset = -0.44F;
         } else if (tile.rotation == 0) {
            yOffset = 0.44F;
         }
      } else {
         xOffset = -0.44F;
      }

      GlStateManager.translate(var2 + 0.5D + (double)xOffset, var4 + 0.5D, var6 + 0.5D + (double)yOffset);
      float f1 = 0.6666667F;
      GlStateManager.color(1.0F, 1.0F, 1.0F);
      if (tile != null) {
         GlStateManager.rotate((float)(90 * tile.rotation), 0.0F, 1.0F, 0.0F);
         if (tile.rotation % 2 == 0) {
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      TextureManager manager = Minecraft.getMinecraft().getTextureManager();
      manager.bindTexture(resource);
      this.model.renderSign();
      GlStateManager.popMatrix();
      if (tile != null && !tile.block.lines.isEmpty() && !this.playerTooFar(tile)) {
         float f3 = 0.0133F * f1;
         GlStateManager.translate(0.0F, 0.5F, 0.065F);
         GlStateManager.scale(f3, -f3, f3);
         GlStateManager.glNormal3f(0.0F, 0.0F, -1.0F * f3);
         GlStateManager.depthMask(false);
         FontRenderer fontrenderer = this.getFontRenderer();
         float lineOffset = 0.0F;
         if (tile.block.lines.size() < 14) {
            lineOffset = (14.0F - (float)tile.block.lines.size()) / 2.0F;
         }

         for(int i = 0; i < tile.block.lines.size(); ++i) {
            String text = ((ITextComponent)tile.block.lines.get(i)).getFormattedText();
            fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, (int)((double)(lineOffset + (float)i) * ((double)fontrenderer.FONT_HEIGHT - 0.3D)), 0);
            if (i == 12) {
               break;
            }
         }

         GlStateManager.depthMask(true);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      }

      GlStateManager.popMatrix();
   }
}
