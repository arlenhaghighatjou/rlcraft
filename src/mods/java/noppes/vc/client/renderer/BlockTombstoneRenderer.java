package noppes.vc.client.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import noppes.vc.blocks.tiles.TileTombstone;
import noppes.vc.client.TextBlockClient;
import noppes.vc.client.models.ModelTombstone1;
import noppes.vc.client.models.ModelTombstone2;
import noppes.vc.client.models.ModelTombstone3;
import org.lwjgl.opengl.GL11;

public class BlockTombstoneRenderer extends BlockRendererBasic {
   private final ModelTombstone1 model = new ModelTombstone1();
   private final ModelTombstone2 model2 = new ModelTombstone2();
   private final ModelTombstone3 model3 = new ModelTombstone3();
   private int meta;

   public BlockTombstoneRenderer(int meta) {
      this.meta = meta;
   }

   public void render(TileEntity var1, double var2, double var4, double var6, float var8, int blockDamage, float partialTick) {
      TileTombstone tile = (TileTombstone)var1;
      int meta = this.meta;
      if (tile != null) {
         meta = tile.getBlockMetadata();
      }

      GlStateManager.enableLighting();
      GlStateManager.disableBlend();
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      if (meta == 2) {
         GlStateManager.scale(1.0F, 1.0F, 1.14F);
      }

      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      if (tile != null) {
         GlStateManager.rotate((float)(90 * tile.rotation), 0.0F, 1.0F, 0.0F);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(Stone);
      if (meta == 0) {
         this.model.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else if (meta == 1) {
         this.model2.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      } else {
         this.model3.render((Entity)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
      }

      if (meta < 2 && !this.playerTooFar(tile)) {
         this.renderText(tile, meta);
      }

      GlStateManager.popMatrix();
   }

   private void renderText(TileTombstone tile, int meta) {
      if (tile != null) {
         if (tile.block == null || tile.hasChanged) {
            tile.block = new TextBlockClient(tile.getText(), 94, true, new Object[]{Minecraft.getMinecraft().player});
            tile.hasChanged = false;
         }

         if (!tile.block.lines.isEmpty()) {
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            float f3 = 0.00665F;
            GlStateManager.translate(0.0F, -0.64F, meta == 0 ? 0.095F : 0.126F);
            GlStateManager.scale(f3, -f3, f3);
            GL11.glNormal3f(0.0F, 0.0F, -1.0F * f3);
            GlStateManager.depthMask(false);
            FontRenderer fontrenderer = this.getFontRenderer();
            float lineOffset = 0.0F;
            if (tile.block.lines.size() < 11) {
               lineOffset = (11.0F - (float)tile.block.lines.size()) / 2.0F;
            }

            for(int i = 0; i < tile.block.lines.size(); ++i) {
               String text = ((ITextComponent)tile.block.lines.get(i)).getFormattedText();
               fontrenderer.drawString(text, -fontrenderer.getStringWidth(text) / 2, (int)((double)(lineOffset + (float)i) * ((double)fontrenderer.FONT_HEIGHT - 0.3D)), 16777215);
               if (i == 13) {
                  break;
               }
            }

            GlStateManager.depthMask(true);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

      }
   }
}
